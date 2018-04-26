package com.util.kvstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Penetration test by using following Threads
        Thread t1 = new Thread(r);
        t1.setName("T1");
        Thread t2 = new Thread(r2);
        t2.setName("T2");
        Thread t3 = new Thread(r3);
        t3.setName("T3");
        Thread t4 = new Thread(r4);
        t4.setName("T4");
        Thread t5 = new Thread(r5);
        t5.setName("T5");
        Thread t6 = new Thread(r6);
        t6.setName("T6");
        Thread t7 = new Thread(r7);
        t6.setName("T7");
        Thread t8 = new Thread(r8);
        t6.setName("T8");
        Thread t9 = new Thread(r9);
        t9.setName("T9");



        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t7.start();
        t8.start();
        t9.start();


    }

    Runnable r =new Runnable() {
        @Override
        public void run() {
            ArrayList<KVPair> list =new ArrayList<KVPair>();
            for(int i=0;i<=100;i++){
                KVPair data = new KVPair("Key"+i, "val");
                list.add(data);
            }
            KVStore.getInstance(getApplicationContext()).putStringSet(list);

        }
    };


    Runnable r2 =new Runnable() {
        @Override
        public void run() {
            ArrayList<KVPair> list =new ArrayList<KVPair>();
            for(int i=101;i<=200;i++){
                KVPair data = new KVPair("Key"+i, "val");
                list.add(data);
            }
            KVStore.getInstance(getApplicationContext()).putStringSet(list);

        }
    };


    Runnable r3 =new Runnable() {
        @Override
        public void run() {
            KVPair data = new KVPair("KeyAlone", "val");
            KVStore.getInstance(getApplicationContext()).putString(data.getK(),data.getV());

        }
    };


    Runnable r4 =new Runnable() {
        @Override
        public void run() {
            KVPair dd = new KVPair("KeyAlone1", "val");
            KVStore.getInstance(getApplicationContext()).putString(dd.getK(),dd.getV());

        }
    };

    Runnable r5 =new Runnable() {
        @Override
        public void run() {

                 ArrayList<String> keys = new ArrayList<>();
                 keys.add("Key1");
                 keys.add("Key2");
                 keys.add("Key3");
                 keys.add("Key4");
                 keys.add("Key5");
                KVStore.getInstance(getApplicationContext()).getStringSet(keys);

        }
    };

    Runnable r6 =new Runnable() {
        @Override
        public void run() {

                ArrayList<String> keys = new ArrayList<>();
                keys.add("Key101");
                keys.add("Key102");
                keys.add("Key103");
                keys.add("Key104");
                keys.add("Key105");
                KVStore.getInstance(getApplicationContext()).getStringSet(keys);

        }
    };
    Runnable r7 =new Runnable() {
        @Override
        public void run() {
            KVStore.getInstance(getApplicationContext()).getString("Key34","defVal");

        }
    };


    Runnable r8 =new Runnable() {
        @Override
        public void run() {
            KVPair dd = new KVPair("KeyAloneN", (long)4.5);
            KVStore.getInstance(getApplicationContext()).putLong(dd.getK(),dd.getLk());

        }
    };

    Runnable r9 =new Runnable() {
        @Override
        public void run() {
            KVStore.getInstance(getApplicationContext()).getLong("KeyAloneN",(long)222);

        }
    };
}
