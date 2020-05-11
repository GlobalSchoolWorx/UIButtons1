package com.edu.worx.global;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

public class GenericFirebaseQuery {
    public interface Callback<T>{
        T run(DataSnapshot in);
    }

    public GenericFirebaseQuery() {}

    @WorkerThread
    public <R> ArrayList<R> queryDatabase(String searchPath, String nodeName, Class<R> clazz) {
        DatabaseReference masterDbReference = FirebaseDatabase.getInstance().getReference(searchPath);

        CountDownLatch latch = new CountDownLatch(1);
        final ResultHolder<ArrayList<R>> resultHolder = new ResultHolder<>();
        Query q = masterDbReference.orderByChild(nodeName);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //resultHolder.result = callback.run(dataSnapshot);
                resultHolder.result = new ArrayList<>();
                for (DataSnapshot hw : dataSnapshot.getChildren()) {
                    if (hw != null) {
                        R hwSet = hw.getValue(clazz);
                        resultHolder.result.add(hwSet);
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("");

            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultHolder.result;
    };

    public class ResultHolder<T>{
        T result;
    }
}


