package com.taxicentral.Activity;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.taxicentral.R;

import java.util.ArrayList;

public class Rough extends AppCompatActivity {
    //aa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rough);


        CardView mCardView = (CardView) findViewById(R.id.mCardView);

        FloatingActionButton fab1 = new FloatingActionButton(Rough.this);
        fab1.show();
        FloatingActionButton fab2 = new FloatingActionButton(Rough.this);
        fab2.setBackgroundResource(R.drawable.ic_action_add);
        FloatingActionButton fab3 = new FloatingActionButton(Rough.this);
        final ArrayList<View> fabChild = new ArrayList<View>();
        fabChild.add(fab1);
        fabChild.add(fab2);
        fabChild.add(fab3);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // fab.addChildrenForAccessibility(fabChild);

            }
        });

        final SwipeDismissBehavior<CardView> swipe
                = new SwipeDismissBehavior();

        swipe.setSwipeDirection(
                SwipeDismissBehavior.SWIPE_DIRECTION_ANY);

        swipe.setListener(
                new SwipeDismissBehavior.OnDismissListener() {
                    @Override
                    public void onDismiss(View view) {
                        Toast.makeText(Rough.this,
                                "Card swiped !!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDragStateChanged(int state) {
                    }
                });

        CoordinatorLayout.LayoutParams coordinatorParams =
                (CoordinatorLayout.LayoutParams) mCardView.getLayoutParams();

        coordinatorParams.setBehavior(swipe);


    }

}
