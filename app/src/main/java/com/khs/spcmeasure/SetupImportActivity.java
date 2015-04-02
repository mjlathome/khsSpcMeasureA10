package com.khs.spcmeasure;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.khs.spcmeasure.library.ActionStatus;
import com.khs.spcmeasure.service.SetupService;

public class SetupImportActivity extends Activity {

	private static final String TAG = "SetupImportActivity";

    // return last Product Id successfully imported
    public static final String RESULT_PROD_ID = "RESULT_PROD_ID";
    private Long lastProdId = null;

    // handle Setup import
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "mMessageReceiver Action = " + action);

            if (SetupService.ACTION_IMPORT.equals(action)) {
                // get extras
                final long prodId = intent.getLongExtra(SetupService.EXTRA_PROD_ID, -1);
                final ActionStatus actStat = (ActionStatus) intent.getSerializableExtra(SetupService.EXTRA_STATUS);

                Log.d(TAG, "mMessageReceiver: prodId = " + prodId + "; actStat = " + actStat);

                switch(actStat) {
                    case WORKING:
                        setProgressBarIndeterminateVisibility(true);
                        break;
                    case OKAY:
                        setProgressBarIndeterminateVisibility(false);
                        lastProdId = prodId;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // send back last imported Product Id, if any
//                                Intent returnIntent = new Intent();
//                                if (lastProdId != null) {
//                                    returnIntent.putExtra(RESULT_PROD_ID, lastProdId);
//                                    SetupImportActivity.this.setResult(RESULT_OK,returnIntent);
//                                } else {
//                                    SetupImportActivity.this.setResult(RESULT_CANCELED,returnIntent);
//                                }
//                                finish();
//                            }
//                        });
                        break;
                    default:
                        setProgressBarIndeterminateVisibility(false);
                        break;
                }
            }
            return;
        }
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // add progress circle to Action Bar - must be done before content added
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_setup_import);

        if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new SetupImportFragment()).commit();
		}				
	}

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        // register local broadcast receiver for Setup import
        IntentFilter setupFilter = new IntentFilter();
        setupFilter.addAction(SetupService.ACTION_IMPORT);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, setupFilter);

        // clear last imported Product Id
        lastProdId = null;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: lastProdId = " + lastProdId);

        // unregister local broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        returnResult();

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: lastProdId = " + lastProdId);

        returnResult();

        super.onBackPressed();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// inflate action bar menu items
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_import_setup, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle action bar menu items
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    // send back last imported Product Id, if any
    private void returnResult() {
        Intent returnIntent = new Intent();
        if (lastProdId != null) {
            returnIntent.putExtra(RESULT_PROD_ID, lastProdId);
            SetupImportActivity.this.setResult(RESULT_OK,returnIntent);
        } else {
            SetupImportActivity.this.setResult(RESULT_CANCELED,returnIntent);
        }
        finish();
    }

}
