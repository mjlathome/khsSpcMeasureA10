package com.khs.spcmeasure.ui;

// 24 Mar 2020 - AndroidX
// import android.app.Activity;

import android.content.Context;
import android.app.AlertDialog;

// 23 Mar 2020 - AndroidX
// import android.app.ListFragment;
import androidx.fragment.app.ListFragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.khs.spcmeasure.R;
import com.khs.spcmeasure.entity.Product;
import com.khs.spcmeasure.library.AlertUtils;
import com.khs.spcmeasure.library.CursorAdapterUtils;
import com.khs.spcmeasure.helper.DBAdapter;
import com.khs.spcmeasure.library.SecurityUtils;
import com.khs.spcmeasure.tasks.DeleteSetupTask;

import java.util.Objects;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSetupListListener}
 * interface.
 */
public class SetupListFragment extends ListFragment {

    private static final String TAG = "SetupListFragment";

    // 25 Mar 2020 - fix static lint warning
    // private static ListView mListView;
    private ListView mListView;

    private OnSetupListListener mListener;

    // TODO: Rename and change types of parameters
    public static SetupListFragment newInstance() {
        // 25 Mar 2002 - removed redundant local var
        // SetupListFragment fragment = new SetupListFragment();
        return new SetupListFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SetupListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    // 24 Mar 2020 - AndroidX
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks
        try {
            mListener = (OnSetupListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProductListListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = getListView();

        // register the listview for a context menu
        registerForContextMenu(mListView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshList();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        selectSetup(id);
    }



    // add in action bar menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.fragment_setup_list, menu);
    }

    // handle action bar menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // 25 Mar 2020 - replaced switch with if
        if(id == R.id.mnuRefresh) {
            refreshList();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    // inflate context menu options
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // 25 Mar 2020 - added null try/catch and Objects.requireNonNull
        try {
            MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
            inflater.inflate(R.menu.context_setup_list, menu);
        } catch (NullPointerException e) {
            throw new NullPointerException(Objects.requireNonNull(getActivity()).toString()
                    + " getMenuInflater() was NULL");
        }

    }

    // handle context menu options
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // get info for item selected
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // handle menu option selected
        switch(item.getItemId()) {
            case R.id.mnuOpen:
                selectSetup(info.id);
                return true;
            case R.id.mnuDelete:
                deleteSetup(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSetupListListener {
        // TODO: Update argument type and name
        // 25 Mar 2020 - removed public modifier as redundant for interface methods
        void onSetupSelected(Long prodId);
    }

    // handle Setup select
    // 25 Mar 2020 - made private was public
    private void selectSetup(long id) {

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onSetupSelected(id);
        }
    }

    // handle Setup delete
    // 25 Mar 2020 - made private was public
    private void deleteSetup(int pos) {

        // check security
        if (!SecurityUtils.isSecurityOk(getActivity(), true)) {
            return;
        }

        Cursor c = (Cursor) mListView.getItemAtPosition(pos);
        final Product p = new DBAdapter(getActivity()).cursorToProduct(c);
        // closing the cursor below was causing the app to crash
        // c.close();

        // confirm with user via dialog
        String message = String.format(getString(R.string.text_mess_delete_setup), p.getName());

        AlertDialog.Builder dlgAlert = AlertUtils.createAlert(getActivity(), getString(R.string.text_warning), message);
        dlgAlert.setPositiveButton(getString(R.string.text_okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            // perform the delete
            new DeleteSetupTask(getActivity()).execute(p.getId());
            }
        });
        dlgAlert.setNegativeButton(getString(R.string.text_cancel), null);
        dlgAlert.show();
    }

    // refresh listview
    public void refreshList() {
        setEmptyText(getString(R.string.text_no_data));

        // start out with a progress indicator.
        setListShown(false);

        // extract all current Products into the listview
        DBAdapter db = new DBAdapter(getActivity());
        db.open();
        Cursor c = db.getAllProducts();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, c,
                new String[] {DBAdapter.KEY_NAME},
                new int[] {android.R.id.text1}, 0);

        // associate adapter with list view
        setListAdapter(adapter);

        mListView = getListView();
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setTextFilterEnabled(true);

        db.close();

        // remove progress indicator.
        setListShown(true);
    }

    // refresh listview and scroll to Product provided
    public void refreshList(long prodId) {
        Log.d(TAG, "refreshList: prodId = " + prodId);
        refreshList();
        if (prodId > 0) {
            final Integer pos = CursorAdapterUtils.getPosForId((CursorAdapter) getListAdapter(), prodId);
            Log.d(TAG, "refreshList: pos = " + pos);
            if (pos != null) {
                getListView().post(new Runnable() {
                    @Override
                    public void run() {
                        getListView().smoothScrollToPosition(pos);
                    }
                });
            }
        }
    }

}
