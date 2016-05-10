package helpshift.com.helpshift;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<HelpShift> {
    public ApplicationTest() {
        super(HelpShift.class);
    }
    public void setUp() throws Exception {
        createApplication();
    }


    @SmallTest
    public void testInsertString(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putString("aaa","bbb");
        assertEquals(KVStore.getInstance(getContext()).getString("aaa","defval"), "bbb");

    }

    @SmallTest
    public void testInsertIntBool(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putBool("aaa",true);
        assertEquals( KVStore.getInstance(getContext()).getBool("aaa",false),true);

    }

    @SmallTest
    public void testInsertIntDate()  {
        String d = "2016-05-11T02:05:40Z";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = null;
        Date date1 = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            assertFalse("parse failed", true);
        }
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putDate("aaa", date);
        assertEquals( KVStore.getInstance(getContext()).getDate("aaa",new Date()),date);
    }

    @SmallTest
    public void testInsertIntNumber(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putInt("aaa",5);
        assertEquals((long) KVStore.getInstance(getContext()).getInt("aaa",999999),(long) 5);

    }

    @SmallTest
    public void testInsertIntNegative(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putInt("aaa",5);
        assertFalse("Falied" , (KVStore.getInstance(getContext()).getInt("aaa",999999) == (long) 6));

    }


    @SmallTest
    public void testInsertLongNumber(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putLong("aaa",5);
        assertEquals((long) KVStore.getInstance(getContext()).getLong("aaa",(long)999999),(long) 5);

    }

    @SmallTest
    public void testInsertLongNegative(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putLong("aaa",5);
        assertFalse("Falied" , (KVStore.getInstance(getContext()).getLong("aaa",999999) == (long) 6));

    }

    @SmallTest
    public void testInsertLongWithInt(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putLong("aaa",5);
        assertFalse("Falied" , (KVStore.getInstance(getContext()).getInt("aaa",999999) == (long) 5));

    }

    @SmallTest
    public void testInsertIntWithLong(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putLong("aaa",(long)5);
        assertFalse("Falied" , (KVStore.getInstance(getContext()).getInt("aaa",999999) == (long) 5));

    }

    @SmallTest
    public void testInsertIntWithString(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putLong("aaa",(long)5);
        assertFalse("Falied" , (KVStore.getInstance(getContext()).getString("aaa","bbbb") == "defVal"));

    }

    @SmallTest
    public void testInsertConnt(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putLong("aaa",(long)5);
        KVStore.getInstance(getContext()).putInt("aaa",6);
        KVStore.getInstance(getContext()).putString("aaa","bbbbbb");
        KVStore.getInstance(getContext()).putBool("aaa",true);
        assertEquals(KVStore.getInstance(getContext()).getKeyCount(),(long)1);
    }

    @SmallTest
    public void testInsertNegative(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putString("aaa","bbb");
        assertFalse("Falied" , KVStore.getInstance(getContext()).getString("aaa","defval").equals("ccc"));

    }

    @SmallTest
    public void testInsertDuplicate(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putString("aaa","bbb");
        KVStore.getInstance(getContext()).putString("aaa","bbbbbbb");
        assertEquals(KVStore.getInstance(getContext()).getString("aaa","defVal"), "bbbbbbb");

    }

    @SmallTest
    public void testStringNull(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putString("aaa","bbb");
        KVStore.getInstance(getContext()).putString("aaa",null);
        assertEquals(KVStore.getInstance(getContext()).getString("aaa","defval"), "bbb");

    }

    @SmallTest
    public void testNumberNull(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putInt("aaa",6);
        KVStore.getInstance(getContext()).putString("aaa",null);
        assertEquals((long) KVStore.getInstance(getContext()).getInt("aaa",99999), 6);
    }

    @SmallTest
    public void testBulkInsert(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        ArrayList<KVPair> list =new ArrayList<KVPair>();
        for(int i=0;i<10;i++){
            KVPair data = new KVPair("Key"+i, "val");
            list.add(data);
        }
        KVStore.getInstance(getContext()).putStringSet(list);
        assertEquals(KVStore.getInstance(getContext()).getKeyCount(), 10);

    }

    @SmallTest
    public void testBulkInsertNegative(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        ArrayList<KVPair> list =new ArrayList<KVPair>();
        for(int i=0;i<10;i++){
            KVPair data = new KVPair("Key"+i, "val");
            list.add(data);
        }
        KVStore.getInstance(getContext()).putStringSet(list);
        assertFalse("Falied" , KVStore.getInstance(getContext()).getKeyCount() == 6);

    }

    @SmallTest
    public void testDelete(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        ArrayList<KVPair> list =new ArrayList<KVPair>();
        for(int i=0;i<10;i++){
            KVPair data = new KVPair("Key"+i, "val");
            list.add(data);
        }
        KVStore.getInstance(getContext()).putStringSet(list);
        assertEquals(KVStore.getInstance(getContext()).getKeyCount(), 10);
        KVStore.getInstance(getContext()).deleteValueForKey("Key3");
        assertEquals(KVStore.getInstance(getContext()).getKeyCount(), 9);

    }

}