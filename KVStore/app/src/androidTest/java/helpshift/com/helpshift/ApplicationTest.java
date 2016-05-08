package helpshift.com.helpshift;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.ArrayList;

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
        assertEquals(KVStore.getInstance(getContext()).getStringForKey("aaa"), "bbb");

    }

    @SmallTest
    public void testInsertStringNumber(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putNumber("aaa",5);
        assertEquals((long) KVStore.getInstance(getContext()).getNumberForKey("aaa"),(long) 5);

    }

    @SmallTest
    public void testInsertNumberNegative(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putNumber("aaa",5);
        assertFalse("Falied" , (KVStore.getInstance(getContext()).getNumberForKey("aaa") == (long) 6));

    }

    @SmallTest
    public void testInsertNegative(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putString("aaa","bbb");
        assertFalse("Falied" , KVStore.getInstance(getContext()).getStringForKey("aaa").equals("ccc"));

    }

    @SmallTest
    public void testInsertDuplicate(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putString("aaa","bbb");
        KVStore.getInstance(getContext()).putString("aaa","bbbbbbb");
        assertEquals(KVStore.getInstance(getContext()).getStringForKey("aaa"), "bbbbbbb");

    }

    @SmallTest
    public void testStringNull(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putString("aaa","bbb");
        KVStore.getInstance(getContext()).putString("aaa",null);
        assertEquals(KVStore.getInstance(getContext()).getStringForKey("aaa"), "bbb");

    }

    @SmallTest
    public void testNumberNull(){
        KVStore.getInstance(getContext()).clearAllKeyValues();
        KVStore.getInstance(getContext()).putNumber("aaa",6);
        KVStore.getInstance(getContext()).putString("aaa",null);
        assertEquals((long) KVStore.getInstance(getContext()).getNumberForKey("aaa"), 6);
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