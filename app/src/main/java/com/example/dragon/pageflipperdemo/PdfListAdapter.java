package com.example.dragon.pageflipperdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dragon.pageflipperdemo.Database.Firebase.PdfDataModel;
import com.example.dragon.pageflipperdemo.Storage.Local.LocalStorageService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Pdflista tárolását és megjelenítését végző recycleview
 */
public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.PdfViewHolder> {

    private List<PdfDataModel> dataSet;
    private Activity context;
    private RecyclerView recyclerView;


    /**
     * viewholder, maiben a egy kártya megjelenik
     */
    public class PdfViewHolder extends RecyclerView.ViewHolder{
        public TextView mPdfTitle;

        public PdfViewHolder(View view){
            super(view);

//            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//                @Override
//                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//                    PopupMenu popup = new PopupMenu(view.getContext(), view);
//                    popup.getMenuInflater().inflate(R.menu.card_menu, popup.getMenu());
//                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem menuItem) {
//                            switch (menuItem.getItemId()){
//                                case R.id.mi_pdfEdit:
//                                    break;
//                                case R.id.mi_pdfDelete:
//                                    break;
//                            }
//                            return false;
//                        }
//                    });
//                    popup.show();
//                }
//            });

            mPdfTitle = view.findViewById(R.id.tv_pdfTitle);
        }
    }

    public PdfListAdapter(ArrayList<PdfDataModel> dataSet, Activity context, RecyclerView recyclerView){
        this.dataSet = dataSet;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public PdfViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.template_pdf_list_item, viewGroup, false);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                PdfDataModel pdfDataModel = dataSet.get(position);
                Uri uri = LocalStorageService.getInstance(context).getPdfById(pdfDataModel.id);

                if(uri == null){
                    return;
                }

                Intent intent = new Intent(context, PdfActivity.class);
                intent.putExtra("uri", uri.toString());
                context.startActivity(intent);
            }
        });

        return new PdfViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PdfViewHolder viewHolder, int i) {
        PdfDataModel pdfDataModel = dataSet.get(i);
        viewHolder.mPdfTitle.setText(pdfDataModel.title);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

//    private void removePdfFromList(final PdfDataModel pdfDataModel) {
//        new AlertDialog.Builder(this)
//                .setTitle("Pdf törlése")
//                .setMessage("Biztos törli a pdf-et?")
//                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dataSet.remove(pdfDataModel);
//                        notifyDataSetChanged();
//                    }
//                })
//                .setNegativeButton("Nem", null)
//                .show();
//    }
}
