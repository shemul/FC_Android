package com.csform.android.fcloud.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csform.android.fcloud.MainActivity;
import com.csform.android.fcloud.R;

import java.io.InputStream;
import java.util.List;

;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> items;

    private int itemLayout;

    public ReviewAdapter(List<Review> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Review item = items.get(position);
        holder.itemView.setTag(item);
        holder.f_name.setText(item.f_name.toUpperCase());

        holder.f_status.setText(item.f_status.toUpperCase());
        //holder.f_email.setText(item.f_email);
        Typeface font = Typeface.createFromAsset(MainActivity.contex.getAssets(), "fonts/fontawesome-webfont.ttf" );
       // holder.statusIcon.setTypeface(font);

       // holder.f_dept.setText(item.f_dept);

        new DownloadImageTask((ImageView) holder.profile_pic)
                .execute("http://fsit.aiub.edu/Files/Uploads/" + item.f_id +".JPG");





        if(item.f_status.equals("Available")) {
            //holder.statusIcon.setTextColor(Color.parseColor("#4caf50"));
            //holder.statusIcon.setText(new String(new char[]{0xf111 }));
            holder.LL1.setBackgroundColor(Color.parseColor("#4ea851"));
        } else if (item.f_status.equals("Busy")) {
            //holder.statusIcon.setTextColor(Color.parseColor("#f44336"));
            //holder.statusIcon.setText(new String(new char[]{0xf111}));
            holder.LL1.setBackgroundColor(Color.parseColor("#f44336"));

        } else if(item.f_status.equals("Gone")) {
//            holder.statusIcon.setTextColor(Color.parseColor("#ff9800"));
//            holder.statusIcon.setText(new String(new char[]{0xf111}));
            holder.LL1.setBackgroundColor(Color.parseColor("#ff9800"));
        }

    }




    @Override
    public int getItemCount() {

        return items.size();
    }



    public void add(Review item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Review item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }



    private static int setColorAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView f_name;
        private TextView f_status;
        private TextView f_email;
        TextView statusIcon;
        ImageView profile_pic ;
        TextView f_dept ;
        private  LinearLayout LL1;

        public ViewHolder(View itemView) {
            super(itemView);
            f_name = (TextView) itemView.findViewById(R.id.f_name);
            f_status = (TextView) itemView.findViewById(R.id.f_status);
            f_email = (TextView) itemView.findViewById(R.id.f_email);
            //statusIcon = (TextView) itemView.findViewById(R.id.status_icon);
            profile_pic = (ImageView) itemView.findViewById(R.id.profilePic);
            //f_dept = (TextView) itemView.findViewById(R.id.f_dept);
            LL1 = (LinearLayout) itemView.findViewById(R.id.LL1);
        }


    }





    public void animateTo(List<Review> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Review> newModels) {
        for (int i = items.size() - 1; i >= 0; i--) {
            final Review model = items.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Review> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Review model = newModels.get(i);
            if (!items.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Review> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Review model = newModels.get(toPosition);
            final int fromPosition = items.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Review removeItem(int position) {
        final Review model = items.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Review model) {
        items.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Review model = items.remove(fromPosition);
        items.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}