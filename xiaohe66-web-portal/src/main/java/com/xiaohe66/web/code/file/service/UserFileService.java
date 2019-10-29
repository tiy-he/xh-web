package com.xiaohe66.web.code.file.service;

import com.xiaohe66.web.base.base.impl.AbstractService;
import com.xiaohe66.web.base.data.CodeEnum;
import com.xiaohe66.web.base.data.Final;
import com.xiaohe66.web.base.exception.XhIoException;
import com.xiaohe66.web.base.exception.XhWebException;
import com.xiaohe66.web.base.util.Check;
import com.xiaohe66.web.base.util.ClassUtils;
import com.xiaohe66.web.base.util.EncoderUtils;
import com.xiaohe66.web.base.util.StrUtils;
import com.xiaohe66.web.code.file.dto.UsrFileDto;
import com.xiaohe66.web.code.file.mapper.UserFileMapper;
import com.xiaohe66.web.code.file.param.UsrFileParam;
import com.xiaohe66.web.code.file.po.CommonFile;
import com.xiaohe66.web.code.file.po.UserFile;
import com.xiaohe66.web.code.file.po.UsrFileDownloadCount;
import com.xiaohe66.web.code.file.po.UsrFileLog;
import com.xiaohe66.web.code.org.helper.UserHelper;
import com.xiaohe66.web.code.org.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xh
 * @time 18-03-12 012
 */
@Service
public class UserFileService extends AbstractService<UserFileMapper, UserFile> {

    private static final Logger LOG = LoggerFactory.getLogger(UserFileService.class);

    private static final Set<String> IMG_TYPE_SET = new HashSet<>(Arrays.asList(".png", ".jpg", ".jpeg", ".bmp", ".ico"));

    private static final char[] FILE_ILLEGAL_CHARS = new char[]{'?', '\\', '/', '*', '\"', ':', '<', '>', '|'};

    private static final int DEFAULT_FILE_TYPE = 0;

    public static final int USR_HEAD_IMG_FILE_TYPE = 1;

    public static final int USR_ARTICLE_IMG_FILE_TYPE = 2;

    private static final int FILE_NAME_MAX_LENGTH = 20;

    private static final int FILE_EXTENSION_MAX_LENGTH = 8;

    /**
     * 2M
     */
    private static final int USR_HEAD_IMG_MAX_BYTE_LENGTH = 1024 * 1024 * 2;

    @Autowired
    private CommonFileService commonFileService;

    @Autowired
    private UsrFileLogService usrFileLogService;

    @Autowired
    private UserService userService;

    public UserFile findByCommonFileId(Integer commonFileId) {
        Check.notEmpty(commonFileId, "commonFileId");
        return baseMapper.findByCommonFileId(commonFileId);
    }

    public Integer findCommonFileId(Integer usrFileId) {
        Check.notEmpty(usrFileId, "usrFileId");
        return baseMapper.findCommonFileId(usrFileId);
    }

    public List<UsrFileDto> findDtoByUsrId(Integer userId) {
        Check.notEmpty(userId, "userId");
        UsrFileParam param = new UsrFileParam();
        param.setCreateId(userId);

        List<UserFile> usrFileList = this.listByParam(param);

        return ClassUtils.convert(UsrFileDto.class, usrFileList, (usrFileDto, usrFile) -> {
            CommonFile commonFile = commonFileService.getById(usrFile.getFileId());

            usrFileDto.setIsFinish(commonFile.getEndTime() != null);

            Integer size = commonFile.getFileByte();
            //todo:需转成可视化单位
            usrFileDto.setFileSize(size + "字节");
        });
    }

    public List<UsrFileDto> findDtoAll(String search, boolean onlyWebmaster) {

        UsrFileParam param = new UsrFileParam();
        if (onlyWebmaster) {
            param.setCreateId(Final.Sys.XIAO_HE_USR_ID);
        }
        if (Check.isNotEmpty(search)) {
            param.setFileName("%" + search + "%");
        }

        List<UserFile> usrFileList = this.listByParam(param);

        return ClassUtils.convert(UsrFileDto.class, usrFileList, (usrFileDto, usrFile) -> {
            Integer size = commonFileService.getById(usrFile.getFileId()).getFileByte();

            //todo:需转成可视化单位
            usrFileDto.setFileSize(size + "字节");
            usrFileDto.setUsrName(userService.getById(usrFile.getCreateId()).getUsrName());
        });
    }

    public List<UsrFileDto> findDtoHotTop5(Integer usrId) {
        List<UsrFileDownloadCount> mapList = usrFileLogService.countDownloadOfMonth(usrId);
        int i = 0;
        final int maxSize = 5;
        List<UsrFileDto> usrFileDtoList = new ArrayList<>(maxSize);
        for (UsrFileDownloadCount downloadCount : mapList) {
            UserFile usrFile = this.getById(downloadCount.getId());
            if (usrFile == null) {
                continue;
            }
            UsrFileDto usrFileDto = ClassUtils.convert(UsrFileDto.class, usrFile);
            usrFileDtoList.add(usrFileDto);
            usrFileDto.setDownloadCount(downloadCount.getCount());
            if (++i >= maxSize) {
                break;
            }
        }
        return usrFileDtoList;
    }

    public void showImg(OutputStream outputStream, Integer userFileId) {
        Check.notEmpty(userFileId, "userFileId");

        UserFile userFile = getById(userFileId);
        if (userFile == null) {
            throw new XhWebException(CodeEnum.B1_OBJ_NOT_EXIST, "userFile is not found, userFileId:" + userFileId);
        }

        //不是图片类型，不返回
        if (!IMG_TYPE_SET.contains(userFile.getExtension())) {
            throw new XhWebException(CodeEnum.B0_ILLEGAL_REQUEST, "请求显示一个非图片类型, userFileId : " + userFileId);
        }

        commonFileService.outputFile(outputStream, userFile.getFileId());
    }

    /**
     * 下载文件
     * todo:需要控制不公开文件的下载权限
     *
     * @param response   HttpServletResponse
     * @param userFileId 用户文件id
     */
    public void downloadFile(HttpServletResponse response, Integer userFileId) throws XhIoException {
        Check.notEmpty(userFileId, "userFileId");

        UserFile userFile = getById(userFileId);
        if (userFile == null) {
            throw new XhWebException(CodeEnum.B1_OBJ_NOT_EXIST, "文件不存在, userFileId : " + userFileId);
        }

        String name = EncoderUtils.urlEncoder(userFile.getFileName()) + userFile.getExtension();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + name);
        try {
            commonFileService.outputFile(response.getOutputStream(), userFile.getFileId());
        } catch (IOException e) {
            throw new XhIoException(e);
        }

        //记录下载日志
        usrFileLogService.save(new UsrFileLog(userFileId));
    }

    public void updateNameById(Integer fileId, String fileName) {
        fileName = fileNameFormat(fileName);
        Check.notEmpty(fileId, "fileId");
        Check.notEmpty(fileName, "fileName");

        UserFile usrFile = new UserFile(fileId, fileName);
        usrFile.setUpdateId(UserHelper.getCurrentUsrId());
        updateById(usrFile);
    }

    /**
     * 文件名格式化
     * 超过20个字符时，截取前32个字符。
     * 此外，如果出现非法字符，则会抛出异常
     *
     * @param fileName 文件名
     * @return 格式化后的文件名
     */
    protected String fileNameFormat(String fileName) {
        fileName = StrUtils.trim(fileName);
        Check.notEmpty(fileName, "fileName");
        for (char fileIllegalChar : FILE_ILLEGAL_CHARS) {
            if (fileName.contains(String.valueOf(fileIllegalChar))) {
                throw new XhWebException(CodeEnum.B1_ILLEGAL_PARAM);
            }
        }
        //文件名字符长度不能超过20
        return fileName.length() > FILE_NAME_MAX_LENGTH ? fileName.substring(0, FILE_NAME_MAX_LENGTH) : fileName;
    }

    /**
     * 文件名格式化
     * 超过20个字符时，截取前20个字符。
     * 此外，如果出现非法字符，则会抛出异常
     *
     * @param extension 文件名
     * @return 格式化后的文件名
     */
    protected String fileExtensionFormat(String extension) {
        extension = StrUtils.trim(extension);
        Check.notEmpty(extension, "extension");
        for (char fileIllegalChar : FILE_ILLEGAL_CHARS) {
            if (extension.contains(String.valueOf(fileIllegalChar))) {
                throw new XhWebException(CodeEnum.B1_ILLEGAL_PARAM);
            }
        }
        //文件名字符长度不能超过20
        return extension.length() > FILE_EXTENSION_MAX_LENGTH ? extension.substring(0, FILE_EXTENSION_MAX_LENGTH) : extension;
    }

}
