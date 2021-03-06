package com.xiaohe66.web.test.base.classutils;

import com.xiaohe66.web.base.base.BasePo;
import com.xiaohe66.web.base.util.ClassUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author xh
 * @date 2018-1-19
 */
public class ClassConvertTest {

//    @Test(expected = XhException.class)
    public void test1(){
//        ClassUtils.convert(null,new Obj1());
    }

    @Test
    public void test2(){
        BasePo po = null;
        Obj2 obj2 = ClassUtils.convert(Obj2.class, po);
        assertEquals(null,obj2);
    }

    @Test
    public void test3(){
        Obj3 obj3 = ClassUtils.convert(Obj3.class,new Obj1());
        assertEquals(null,obj3);
    }

    @Test
    public void test4(){
        int integer1 = 1;
        int integer2 = 2;
        Integer createId = 3;
        String str1 = "str1";
        String str2 = "str2";

        Obj1 obj1 = new Obj1(integer1,str1,str2,integer2);
        obj1.setId(100);
        obj1.setCreateId(createId);
        Obj2 obj2 = ClassUtils.convert(Obj2.class,obj1);

        assertEquals(new Integer(100),obj2.getId());
        assertEquals(integer1,obj2.getInteger());
        assertEquals(str1,obj2.getString());
        assertEquals(null,obj2.getObj1NotHave());
        assertEquals(0,obj2.getTypeDifferent().intValue());
        assertEquals(createId,obj2.getCreateId());
    }
}
