package com.example.chataround;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ListViewItem> itemList;
    private FirebaseController firebaseController;


    public ItemAdapter(Activity activity, List<ListViewItem> itemList){
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    public ListViewItem getListViewItem(int i){ return itemList.get(i); }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null){
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            view = inflater.inflate(R.layout.listview_item, null);
        }
        TextView name = view.findViewById(R.id.itemName);
        TextView time = view.findViewById(R.id.itemTime);
        TextView message = view.findViewById(R.id.itemMessage);
        final TextView commentCount = view.findViewById(R.id.commentCount);
        final TextView likeCount = view.findViewById(R.id.likeCount);
        ImageView image = view.findViewById(R.id.itemImage);
        CardView cardView = view.findViewById(R.id.cardView);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button likeButton = view.findViewById(R.id.likeButton);

        final Context activity = view.getContext();
        final ListViewItem item = itemList.get(i);
        name.setText(item.getName());
        time.setText(item.getTime());
        if(item.getComments()!=0) commentCount.setText(String.valueOf(item.getComments()));
        if(item.getLikes()!=0) likeCount.setText(String.valueOf(item.getLikes()));

        // Check for empty message
        if (!TextUtils.isEmpty(item.getMessage())) {
            message.setText(item.getMessage());
            message.setVisibility(View.VISIBLE);
        } else {
            // message is empty, remove from view
            message.setVisibility(View.GONE);
        }

        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        if(item.getIsLoading()){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }

        // Check image
        if(item.getImageId()!=null){
            cardView.setVisibility(View.VISIBLE);
        }else{
            cardView.setVisibility(View.GONE);
        }

        if (item.getImage() != null) {
            image.setImageBitmap(item.getImage());
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseController = FirebaseController.getInstance();
                firebaseController.initialize();
                firebaseController.getMyDatabase().child("Liked").child(item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(firebaseController.getUsername()).getValue()==null){
                            item.setLikes(item.getLikes()+1);
                            firebaseController.getMyDatabase().child("Messages").child(item.getId()).child("likes").setValue(item.getLikes());
                            firebaseController.getMyDatabase().
                                    child("Liked").child(item.getId()).child(firebaseController.getUsername()).setValue("+");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Delete message...");
                builder.setMessage("Do you want to delete this message?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseController = FirebaseController.getInstance();
                        firebaseController.initialize();
                        firebaseController.getMyDatabase().child("Messages").child(item.getId()).removeValue();
                        if(item.getComments()!=0) {
                            firebaseController.getMyDatabase().child("Comments").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (item.getId().equals(snapshot.child("postId").getValue())) {
                                            snapshot.getRef().removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        if(item.getLikes()!=0) {
                            firebaseController.getMyDatabase().child("Liked").child(item.getId()).removeValue();
                        }
                        if(item.getImage()!=null) {
                            StorageReference ref = firebaseController.getMyStorage().child(item.getImageId());
                            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(activity, "Error deleting an image", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        itemList.remove(item);
                        notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        return view;
    }

}
