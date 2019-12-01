/*
 * Copyright 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.core.codelab.cloudanchor;

import android.content.Context;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/** Helper class for Firebase storage of cloud anchor IDs. */
class StorageManager {

    MainActivity ma = new MainActivity();

  /** Listener for a new Cloud Anchor ID from the Firebase Database. */
  interface CloudAnchorIdListener {
    void onCloudAnchorIdAvailable(String cloudAnchorId);
  }

  /** Listener for a new short code from the Firebase Database. */
  interface ShortCodeListener {
    void onShortCodeAvailable(Integer shortCode);
  }

  private static final String TAG = StorageManager.class.getName();
  private static final String KEY_ROOT_DIR = "Anchors";
  private static final String KEY_NEXT_SHORT_CODE = "next_short_code";
  private static final String KEY_PREFIX = "anchor;";
  private static final String KEY_ID = "id";
  private static final String KEY_LABEL = "Label";
  private static final String KEY_TEXT = "Text";
  private static final int INITIAL_SHORT_CODE = 1001;
  private final DatabaseReference rootRef;

  /** Constructor that initializes the Firebase connection. */
  StorageManager(Context context) {
    FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
      FirebaseDatabase fb  = FirebaseDatabase.getInstance(firebaseApp);
      DatabaseReference dr = fb.getReference();
    rootRef = dr.child(KEY_ROOT_DIR);
    DatabaseReference.goOnline();
  }

  /** Gets a new short code that can be used to store the anchor ID. */
  void nextShortCode(ShortCodeListener listener) {
    // Run a transaction on the node containing the next short code available. This increments the
    // value in the database and retrieves it in one atomic all-or-nothing operation.
    rootRef
        .child(KEY_NEXT_SHORT_CODE)
        .runTransaction(
            new Transaction.Handler() {
              @Override
              public Transaction.Result doTransaction(MutableData currentData) {
                Integer shortCode = currentData.getValue(Integer.class);
                if (shortCode == null) {
                  // Set the initial short code if one did not exist before.
                  shortCode = INITIAL_SHORT_CODE - 1;
                }
                currentData.setValue(shortCode + 1);
                return Transaction.success(currentData);
              }

              @Override
              public void onComplete(
                  DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (!committed) {
                  Log.e(TAG, "Firebase Error", error.toException());
                  listener.onShortCodeAvailable(null);
                } else {
                  listener.onShortCodeAvailable(currentData.getValue(Integer.class));
                }
              }
            });
  }

  /** Stores the cloud anchor ID in the configured Firebase Database. */
  void storeUsingShortCode(int shortCode, String cloudAnchorId) {

    rootRef.child(KEY_PREFIX + shortCode).setValue(cloudAnchorId);
    rootRef.child(KEY_PREFIX + shortCode).child(KEY_ID).setValue(cloudAnchorId);
    rootRef.child(KEY_PREFIX + shortCode).child(KEY_LABEL).setValue("Dış Mekan");
    rootRef.child(KEY_PREFIX + shortCode).child(KEY_TEXT).setValue("Satılık Araba");

  }

  /**
   * Retrieves the cloud anchor ID using a short code. Returns an empty string if a cloud anchor ID
   * was not stored for this short code.
   */
  void getCloudAnchorId(int shortCode, CloudAnchorIdListener listener) {
    rootRef
        .child(KEY_PREFIX + shortCode).child(KEY_ID)
        .addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                // Listener invoked when the data is successfully read from Firebase.
                listener.onCloudAnchorIdAvailable(String.valueOf(dataSnapshot.getValue()));
              }

              @Override
              public void onCancelled(DatabaseError error) {
                Log.e(
                    TAG,
                    "The Firebase operation for getCloudAnchorId was cancelled.",
                    error.toException());
                listener.onCloudAnchorIdAvailable(null);
              }
            });
  }

}
