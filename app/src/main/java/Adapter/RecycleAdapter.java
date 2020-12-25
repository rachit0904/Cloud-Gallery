package Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.docshot.R;
import com.project.docshot.imagePreview;
import com.squareup.picasso.Picasso;

import java.util.List;

import Data.ImageFiles;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    private Context context;
    private List<ImageFiles> imageFiles;

    public RecycleAdapter(Context context, List<ImageFiles> imageFiles) {
        this.context = context;
        this.imageFiles = imageFiles;
    }

    @NonNull
    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcard,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position) {
                ImageFiles files=imageFiles.get(position);
                holder.fileName.setText(files.getTitle());
                holder.date.setText(files.getDate());
                holder.time.setText(files.getTime());
                holder.pages.setText(files.getPages());
                String URL=files.getImageURL();
                Picasso.
                with(context)
                .load(URL)
                .noFade()
                .fit()
                .placeholder(R.drawable.legal) // can also be a drawable
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageButton deleteBtn;
        public TextView fileName,date,time,pages;
        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context=ctx;
            image=itemView.findViewById(R.id.fileImage);
            fileName=itemView.findViewById(R.id.fileName);
            date=itemView.findViewById(R.id.fileDate);
            time=itemView.findViewById(R.id.fileTime);
            pages=itemView.findViewById(R.id.pages);
            deleteBtn=itemView.findViewById(R.id.delete);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    ImageFiles files= imageFiles.get(pos);
                    Intent intent=new Intent(ctx, imagePreview.class);
                    intent.putExtra("imageUrl",files.getImageURL());
                    context.startActivity(intent);
                }
            });
        }
    }
}
