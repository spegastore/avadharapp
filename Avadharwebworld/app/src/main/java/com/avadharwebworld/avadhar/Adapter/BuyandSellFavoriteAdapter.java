package com.avadharwebworld.avadhar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avadharwebworld.avadhar.Activity.ViewBuyandSellDetails;
import com.avadharwebworld.avadhar.Application.AppController;
import com.avadharwebworld.avadhar.Data.BuyandSellItem;
import com.avadharwebworld.avadhar.R;
import com.avadharwebworld.avadhar.Support.Constants;
import com.avadharwebworld.avadhar.Support.DatabaseInfo;
import com.avadharwebworld.avadhar.Support.FeedImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 */

public class BuyandSellFavoriteAdapter extends RecyclerView.Adapter<BuyandSellFavoriteAdapter.ViewHolder> {
    private Activity mActivity;
    private Context mContext;
    private SharedPreferences sp;
    private String sp_uid,profileid;
    int screenWidth,screenHeight;
    Display display;
    Point size;

    private List<BuyandSellItem> buyandSellItems;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public BuyandSellFavoriteAdapter(Activity activity,Context context,List<BuyandSellItem> item){
        this.mActivity=activity;
        this.mContext=context;
        this.buyandSellItems=item;
    }
    @Override
    public BuyandSellFavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buyandsell_layout, parent, false);
        return new BuyandSellFavoriteAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BuyandSellFavoriteAdapter.ViewHolder holder, int position) {
        sp=mContext.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        sp_uid=sp.getString(Constants.UID,"");
        profileid=sp.getString(Constants.PROFILEID,"");
        BuyandSellItem bind=buyandSellItems.get(position);
        if(!bind.getImage().equals("null")) holder.image.setImageUrl(bind.getImage(),imageLoader);
        holder.Name.setText(bind.getName());
        holder.price.setText(bind.getPrice());
        holder.delete.setVisibility(View.VISIBLE);
        holder.favorite.setVisibility(View.GONE);

//        holder.religion.setText(bind.getReligion());

        Log.e("At Matrimony adapter","fetch");
    }

    @Override
    public int getItemCount() {
        return buyandSellItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Name,type,price,location1;
        public FeedImageView image;
        public ImageView favorite,thump,delete;

        public ViewHolder(final View itemView) {
            super(itemView);

            price=(TextView)itemView.findViewById(R.id.tv_buyandsell_price);
            Name=(TextView)itemView.findViewById(R.id.tv_buyandsell_name);

            favorite=(ImageView)itemView.findViewById(R.id.iv_buyandsell_favorite);
            thump=(ImageView)itemView.findViewById(R.id.iv_buyandsell_thump);
            image=(FeedImageView)itemView.findViewById(R.id.iv_buyandsell_feed);
            delete=(ImageView)itemView.findViewById(R.id.iv_buyandsell_delete);
            WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            display=wm.getDefaultDisplay();
            size=new Point();
            display.getSize(size);
            screenHeight=size.y;
            screenWidth=size.x;
            LinearLayout.LayoutParams parmsIMG = new LinearLayout.LayoutParams(screenWidth/2,150);
//            InstImage.setLayoutParams(parmsIMG);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"item position " +String.valueOf(getLayoutPosition()),Toast.LENGTH_SHORT).show();
                    BuyandSellItem eduItem=buyandSellItems.get(getLayoutPosition());
//                    eduItem.getId();
                    Intent intent=new Intent(mContext, ViewBuyandSellDetails.class);
                    intent.setFlags( FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra("id",  eduItem.getId());

                    mContext.startActivity(intent);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BuyandSellItem eduItem=buyandSellItems.get(getLayoutPosition());
                    deleteFavorite(sp_uid, eduItem.getId());
                    buyandSellItems.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    notifyItemRangeChanged(getAdapterPosition(),buyandSellItems.size());

                }
            });

            thump.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(mContext,"thump clicked",Toast.LENGTH_SHORT).show();

                    BuyandSellItem eduItem=buyandSellItems.get(getLayoutPosition());
                    setInterestSend(sp_uid,eduItem.getMprofileid());
                }
            });



        }
    }
    public void  deleteFavorite(final String uid, final String pid){
        final String[] res = new String[1];
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DatabaseInfo.BuyandsellDeleteFavoriteURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volleyresponse",response.toString());

                        String   favoriteResult =response.toString();


                        Toast.makeText(mContext,favoriteResult,Toast.LENGTH_SHORT).show();

//                        setAnimationForFavoriteText(response,layout);
                        // Toast.makeText(getActivity().getApplication(),response,Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext,error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pid", String.valueOf(pid));
                params.put("uid",String.valueOf(uid));
                return params;
            }

        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }
    public void  setInterestSend(final String uid,final String rid){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DatabaseInfo.BuyandSellInterestsendURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volleyresponse",response.toString());

                        String   favoriteResult =response.toString();


                        Toast.makeText(mContext,favoriteResult,Toast.LENGTH_SHORT).show();

//                        setAnimationForFavoriteText(response,layout);
                        // Toast.makeText(getActivity().getApplication(),response,Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext,error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("logid",String.valueOf(uid));
                params.put("pid", String.valueOf(rid));


                return params;
            }

        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

}
