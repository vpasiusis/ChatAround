package com.example.chataround;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

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
        firebaseController = FirebaseController.getInstance();
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
        final ImageView image = view.findViewById(R.id.itemImage);
        final CardView cardView = view.findViewById(R.id.cardView);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        final Button likeButton = view.findViewById(R.id.likeButton);
        final Button commentButton = view.findViewById(R.id.commentButton);
        final ImageView imageViewAvatar = view.findViewById(R.id.itemAvatar);
        final ImageView defaultImageViewAvatar = view.findViewById(R.id.defaultitemAvatar);
        final ListViewItem item = itemList.get(i);
        name.setText(item.getName());
        String realtime = firebaseController.diffTime(item.getTime());
        time.setText(realtime);
        commentCount.setText(firebaseController.diffCount(item.getComments()));
        likeCount.setText(firebaseController.diffCount(item.getLikes()));

        if(!item.getLiked()){
            likeButton.setBackgroundResource(R.drawable.like);
        }else{
            likeButton.setBackgroundResource(R.drawable.liked);
        }

        // Check for empty message
        if (!TextUtils.isEmpty(item.getMessage())) {
            message.setText(item.getMessage());
            message.setVisibility(View.VISIBLE);
        } else {
            // message is empty, remove from view
            message.setVisibility(View.GONE);
        }
        if(item.getAvatarId()!=null){
            imageViewAvatar.setVisibility(View.VISIBLE);
            defaultImageViewAvatar.setVisibility(View.INVISIBLE);
            PicassoCache.getPicassoInstance(activity).load(item.getAvatarId()).
                    networkPolicy(NetworkPolicy.OFFLINE).into(imageViewAvatar, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    PicassoCache.getPicassoInstance(activity).load(item.getAvatarId()).
                            into(imageViewAvatar, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                }
                            });
                }
            });
        }else {
            imageViewAvatar.setVisibility(View.INVISIBLE);
            defaultImageViewAvatar.setVisibility(View.VISIBLE);
            defaultImageViewAvatar.setBackgroundResource(R.drawable.ic_person_black_24dp);
        }
        firebaseController.getMyDatabase().child("Liked").child(item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if("+".equals(dataSnapshot.child(firebaseController.getCurrentUser().getName()).getValue())){
                    likeButton.setBackgroundResource(R.drawable.liked);
                    item.setLiked(true);
                }
                else {
                    likeButton.setBackgroundResource(R.drawable.like);
                    item.setLiked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Check image
        if(item.getImageId()!=null){
            cardView.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            PicassoCache.getPicassoInstance(activity).load(item.getImageId()).
                    networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    PicassoCache.getPicassoInstance(activity).load(item.getImageId()).
                            into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
                }
            });
        }else{
            cardView.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
        }

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CommentsActivity.class);
                firebaseController.setCurrentSelectedItem(item);
                intent.putExtra("keyboard", true);
                activity.startActivity(intent);
            }
        });

        if(item.getName().equals(firebaseController.getCurrentUser().getName())
                ||firebaseController.getCurrentUser().getType()==99){
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseController.getMyDatabase().child("Liked").child(item.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(firebaseController.getCurrentUser().getName()).getValue()==null){
                            likeButton.setBackgroundResource(R.drawable.liked);
                            item.setLiked(true);
                            item.setLikes(item.getLikes()+1);
                            firebaseController.getMyDatabase().child("Messages").child(item.getId()).child("likes").setValue(item.getLikes());
                            firebaseController.getMyDatabase().
                                    child("Liked").child(item.getId()).child(firebaseController.getCurrentUser().getName()).setValue("+");
                            firebaseController.updateLikes(item.getName(),true,1);

                        }else {
                            likeButton.setBackgroundResource(R.drawable.like);
                            item.setLiked(false);
                            item.setLikes(item.getLikes()-1);
                            firebaseController.getMyDatabase().child("Messages").child(item.getId()).child("likes").setValue(item.getLikes());
                            firebaseController.getMyDatabase(). child("Liked").child(item.getId()).child(firebaseController.getCurrentUser().getName()).removeValue();
                            firebaseController.updateLikes(item.getName(),false,1);

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
                        firebaseController.getMyDatabase().child("Messages").child(item.getId()).
                                removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                activity.finish();
                                activity.overridePendingTransition(0, 0);
                                activity.startActivity(activity.getIntent());
                                activity.overridePendingTransition(0, 0);
                                firebaseController.updatePosts(item.getName(),false);
                            }
                        });
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
                            firebaseController.updateLikes(item.getName(),false,item.getLikes());
                        }
                        if(item.getImageId()!=null) {
                            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImageId());
                            ref.delete();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.getName().equals(firebaseController.getCurrentUser().getName())) {
                    firebaseController.updateCurrentUser(true, activity);
                }
                else {
                    firebaseController.openClickedUser(item.getName(),activity);
                }


            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //someday, someday...
            }
        });

        return view;
    }




}
