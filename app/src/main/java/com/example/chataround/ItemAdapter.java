package com.example.chataround;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

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

        if (inflater == null){
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            view = inflater.inflate(R.layout.listview_item, null);
        }
        TextView name = view.findViewById(R.id.itemName);
        TextView time = view.findViewById(R.id.itemTime);
        TextView message = view.findViewById(R.id.itemMessage);
        ImageView image = view.findViewById(R.id.itemImage);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        final Context activity = view.getContext();
        final ListViewItem item = itemList.get(i);
        name.setText(item.getName());
        time.setText(item.getTime());

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
        if (item.getImage() != null) {
            image.setImageBitmap(item.getImage());
            image.setVisibility(View.VISIBLE);
        } else {
            image.setVisibility(View.GONE);
        }

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
                        firebaseController.getMyDatabase().child(item.getId()).removeValue();
                        if(item.getImage()!=null) {
                            StorageReference ref = firebaseController.getMyStorage().child(item.getMessage());
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
