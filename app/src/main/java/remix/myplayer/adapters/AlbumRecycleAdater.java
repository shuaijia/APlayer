package remix.myplayer.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.ref.WeakReference;
import java.util.Objects;

import remix.myplayer.R;
import remix.myplayer.activities.MainActivity;
import remix.myplayer.fragments.AlbumRecyleFragment;
import remix.myplayer.utils.Utility;

/**
 * Created by Remix on 2015/12/20.
 */
public class AlbumRecycleAdater extends RecyclerView.Adapter<AlbumRecycleAdater.ViewHolder>  {
    private Cursor mCursor;
    private Context mContext;
    private boolean mScrollState = false;
    private static Bitmap mBmp;
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public void setScrollState(boolean isScroll)
    {
        mScrollState = isScroll;
    }
    private OnItemClickLitener mOnItemClickLitener;

    public AlbumRecycleAdater(Cursor cursor, Context context) {
        this.mCursor = cursor;
        this.mContext = context;
    }

    public void setCursor(Cursor mCursor) {
        this.mCursor = mCursor;
        notifyDataSetChanged();
    }


    //<Params, Progress, Result>
    class AsynLoadImage extends AsyncTask<Integer,Integer,Object>
    {
//        private final WeakReference mImageView;
//        private final ImageView mImage;
        private final SimpleDraweeView mImage;
        public AsynLoadImage(SimpleDraweeView imageView)
        {
//            mImageView = new WeakReference(imageView);
            mImage = imageView;
        }
        @Override
        protected Object doInBackground(Integer... params) {
//            return Utility.CheckBitmapByAlbumId(params[0],false);
            return Utility.CheckUrlByAlbumId(params[0]);
//                return params[0];
        }
        @Override
        protected void onPostExecute(Object url) {
            Uri uri = Uri.parse("file:///" + (String)url);
            if(url != null && mImage != null);
                mImage.setImageURI(uri);
//                mImage.setImageBitmap((Bitmap)url);
//                mImage.setImageURI(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), (int)url));

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_recycle_item, null, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(mCursor.moveToPosition(position))
        {
            holder.mText1.setText(mCursor.getString(AlbumRecyleFragment.mAlbumIndex));
            holder.mText2.setText(mCursor.getString(AlbumRecyleFragment.mArtistIndex));
            AsynLoadImage task = new AsynLoadImage(holder.mImage);
//            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mCursor.getInt(AlbumRecyleFragment.mAlbumIdIndex));
            task.execute(mCursor.getInt(AlbumRecyleFragment.mAlbumIdIndex));
//            holder.mImage.setImageURI(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), mCursor.getInt(AlbumRecyleFragment.mAlbumIdIndex)));
            if(mOnItemClickLitener != null)
            {
                holder.mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        mOnItemClickLitener.onItemClick(holder.mImage,pos);
                    }
                });
            }
            if(holder.mButton != null)
            {
                holder.mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu = new PopupMenu(mContext,holder.mButton);
                        MainActivity.mInstance.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.exit:
                                        popupMenu.dismiss();
                                        break;
                                    default:
                                        Toast.makeText(mContext, "Click " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            }
                        });
                        popupMenu.setGravity(Gravity.END );
                        popupMenu.show();
                    }
                });
            }
        }
    }
    @Override
    public int getItemCount() {
        if(mCursor != null)
            return mCursor.getCount();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mText1;
        public final TextView mText2;
//        public final ImageView mImage;
        public final ImageButton mButton;
        public final SimpleDraweeView mImage;
        public ViewHolder(View v) {
            super(v);
            mText1 = (TextView)v.findViewById(R.id.recycleview_text1);
            mText2 = (TextView)v.findViewById(R.id.recycleview_text2);
//            mImage = (ImageView)v.findViewById(R.id.recycleview_simpleiview);
            mImage = (SimpleDraweeView)v.findViewById(R.id.recycleview_simpleiview);
            mButton = (ImageButton)v.findViewById(R.id.recycleview_button);
        }

    }
}