package com.example.mobilalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private static final String LOG_TAG = CartActivity.class.getName();
    private FirebaseUser user;
    private ArrayList<TicketItem> cart;
    private RecyclerView mRecyclerView;
    private int gridNumber = 1;
    private boolean viewRow = true;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private CartItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "auth success");
        } else {
            Log.d(LOG_TAG, "auth failed");
            finish();
        }
        mRecyclerView = findViewById(R.id.recyclerviewcart);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        cart = new ArrayList<>();
        mAdapter = new CartItemAdapter(this, cart);
        mRecyclerView.setAdapter(mAdapter);
        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection(user.toString());
        loadCartData();
    }

    private void loadCartData() {
        cart.clear();

        mItems.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                TicketItem ticketItem = document.toObject(TicketItem.class);
                cart.add(ticketItem);
            }
            // Notify the adapter of the change.
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cart_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.goback) {
            Log.d(LOG_TAG, "Going back!");
            finish();
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

    private void changeSpanCount(MenuItem item, int viewgrid, int i) {
        viewRow = !viewRow;
        item.setIcon(viewgrid);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(i);
    }

    private void showCart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void deleteTicket(TicketItem currentItem) {
        //item deletion from cart
        currentItem.setIn_cart(0);
        mFirestore.collection(user.toString()).document(currentItem.getBandname()).update("in_cart", 0);
        mItems.document(currentItem.getBandname()).delete().addOnSuccessListener(aVoid -> {
                    Log.d("Activity", "Document successfully deleted!");
                    currentItem.setIn_cart(0);
                    mFirestore.collection(user.toString()).document(currentItem.getBandname()).update("in_cart", 0);
                    finish();
                    startActivity(new Intent(this, CartActivity.class));
                    overridePendingTransition(0, 0);
                })
                .addOnFailureListener(e -> Log.w("Activity", "Error deleting document", e));
    }

}
