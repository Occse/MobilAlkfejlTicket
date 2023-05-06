package com.example.mobilalk;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ConcertListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ConcertListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private ArrayList<TicketItem> cart;

    private NotificationHandler mNotificationHandler;
    private RecyclerView mRecyclerView;
    private ArrayList<TicketItem> mItemsList;
    private TicketItemAdapter mAdapter;
    private FrameLayout redCircle;
    private TextView contentTextView;
    private int cartItems = 0;
    private int gridNumber = 1;
    private boolean viewRow = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concert_list);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "auth success");
        } else {
            Log.d(LOG_TAG, "auth failed");
            finish();
        }
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemsList = new ArrayList<>();
        cart = new ArrayList<>();
        mAdapter = new TicketItemAdapter(this, mItemsList);
        mRecyclerView.setAdapter(mAdapter);
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Tickets");
        readData();
        mNotificationHandler=new NotificationHandler(this);
    }

    //firebase data read
    private void readData() {
        mItemsList.clear();

        mItems.orderBy("bandname").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                TicketItem ticketItem = document.toObject(TicketItem.class);
                mItemsList.add(ticketItem);
            }
            // Notify the adapter of the change.
            mAdapter.notifyDataSetChanged();
        });
    }

    private void loadCartData() {
        cartItems=0;
        cart.clear();
        CollectionReference mItemsUser = mFirestore.collection(user.toString());
        mItemsUser.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                TicketItem ticketItem = document.toObject(TicketItem.class);
                cart.add(ticketItem);
            }
            // Notify the adapter of the change.
            for (TicketItem ticket: cart) {
                cartItems+=ticket.getIn_cart();
            }
            if (0 < cartItems) {
                contentTextView.setText(String.valueOf(cartItems));
            } else {
                contentTextView.setText("");
            }
            redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
        });
        cart.clear();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ticket_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (item.getItemId() == R.id.cart) {
            Log.d(LOG_TAG, "Cart clicked!");
            showCart();
            return true;
        } else if (item.getItemId() == R.id.viewSelector) {
            if (!viewRow) {
                changeSpanCount(item, R.drawable.viewgrid, 1);
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink);
                findViewById(R.id.viewSelector).startAnimation(animation);
            } else {
                changeSpanCount(item, R.drawable.view, 2);
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink);
                findViewById(R.id.viewSelector).startAnimation(animation);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void showCart(){
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
    private void changeSpanCount(MenuItem item, int viewgrid, int i) {
        viewRow = !viewRow;
        item.setIcon(viewgrid);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(i);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = rootView.findViewById(R.id.view_alert_count_textview);
        rootView.setOnClickListener(v -> onOptionsItemSelected(alertMenuItem));

        return super.onPrepareOptionsMenu(menu);
    }

    //create user cart in database
    //updating in_cart amount
    public void updateAlertIcon(TicketItem item) {
        cartItems = (cartItems + 1);
        if(!cart.contains(item)) {
            item.setIn_cart(1);
            cart.add(item);
        }
        else{
            cart.get(cart.indexOf(item)).setIn_cart(item.getIn_cart()+1);
        }
        //creating user collection
        mFirestore.collection(user.toString()).document(item.getBandname()).set(item);
        //updating cartelement
        mFirestore.collection(user.toString()).document(item.getBandname()).update("in_cart",(item.getIn_cart()))
                .addOnFailureListener(fail -> {
            Toast.makeText(this, "Item " + item.getBandname() + " cannot be changed.", Toast.LENGTH_LONG).show();
        });
        if (0 < cartItems) {
            contentTextView.setText(String.valueOf(cartItems));
        } else {
            contentTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);
        String msg = item.getBandname()+" concert ticket placed in cart.";

        mNotificationHandler.send(msg);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        loadCartData();
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}