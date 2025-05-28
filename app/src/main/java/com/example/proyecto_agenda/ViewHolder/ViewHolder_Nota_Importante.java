package com.example.proyecto_agenda.ViewHolder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_agenda.R;

public class ViewHolder_Nota_Importante extends RecyclerView.ViewHolder {

    private static final String TAG = "ViewHolder_Nota_Importante";
    private ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public ViewHolder_Nota_Importante(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, getBindingAdapterPosition());
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemLongClick(view, getBindingAdapterPosition());
                }
                return false;
            }
        });
    }

    public void setItemData(Context context, String idNota, String uidUsuario, String correoUsuario,
                            String fechaHoraRegistro, String titulo, String descripcion, String fechaNota,
                            String estado) {
        // Agrega un log para verificar que se está llamando a setItemData()
        Log.d(TAG, "setItemData: Estableciendo datos en el ViewHolder");

        // DECLARAR LAS VISTAS
        TextView idNotaItem, uidUsuarioItem, correoUsuarioItem, fechaHoraRegistroItem, tituloItem,
                descripcionItem, fechaItem, estadoItem;

        // Obtener las vistas del layout del item
        idNotaItem = itemView.findViewById(R.id.Id_nota_Item_I);
        uidUsuarioItem = itemView.findViewById(R.id.Uid_Usuario_Item_I);
        correoUsuarioItem = itemView.findViewById(R.id.Correo_usuario_Item_I);
        fechaHoraRegistroItem = itemView.findViewById(R.id.Fecha_hora_registro_Item_I);
        tituloItem = itemView.findViewById(R.id.Titulo_Item_I);
        descripcionItem = itemView.findViewById(R.id.Descripcion_Item_I);
        fechaItem = itemView.findViewById(R.id.Fecha_Item_I);
        estadoItem = itemView.findViewById(R.id.Estado_Item_I);

        // SETEAR LA INFORMACIÓN DENTRO DEL ITEM
        idNotaItem.setText(idNota);
        uidUsuarioItem.setText(uidUsuario);
        correoUsuarioItem.setText(correoUsuario);
        fechaHoraRegistroItem.setText(fechaHoraRegistro);
        tituloItem.setText(titulo);
        descripcionItem.setText(descripcion);
        fechaItem.setText(fechaNota);
        estadoItem.setText(estado);
    }
}




//GESTIONAMOS EL COLOR DEL ESTADO
//        if (estado.equals("Finalizado")){
//            Tarea_Finalizada_Item.setVisibility(View.VISIBLE);
//        }else {
//            Tarea_No_Finalizada_Item.setVisibility(View.VISIBLE);
//        }




//import android.content.Context;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.proyecto_agenda.R;
//
//public class ViewHolder_Nota_Importante extends RecyclerView.ViewHolder {
//
//    View mView;
//
//    private ViewHolder_Nota_Importante.ClickListener mClickListener;
//
//    public interface ClickListener{
//        void onItemClick(View view, int position); //Se deberia de ejecutarcuando presionesmo el item
//        void onItemLongClick(View view, int position); //Se deberia de ejecutar cuado presionas un rato el item
//    }
//
//    public void setOnClickListener(ViewHolder_Nota_Importante.ClickListener clickListener){
//        mClickListener = clickListener;
//    }
//
//    public ViewHolder_Nota_Importante(@NonNull View itemView) {
//        super(itemView);
//        mView = itemView;
//
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mClickListener.onItemClick(view, getAdapterPosition()); //getBindingAdapterPosition()
//            }
//        });
//
//        itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                mClickListener.onItemLongClick(view, getAdapterPosition());
//                return false;
//            }
//        });
//    }
//    public void SetearDatos(Context context, String id_nota , String uid_usuario, String correo_usuario,
//                            String fecha_hora_registro, String titulo, String descripcion, String fecha_nota,
//                            String estado){
//
//        //DECLARAR LAS VISTAS
//        TextView Id_nota_Item, Uid_Usuario_Item, Correo_usuario_Item,Fecha_hora_registro_Item,Titulo_Item,
//                Descripcion_Item, Fecha_Item, Estado_Item;
//
////        ImageView Tarea_Finalizada_Item, Tarea_No_Finalizada_Item;
//
//        //ESTABLECER LA CONEXIÓN CON EL ITEM
//        Id_nota_Item = mView.findViewById(R.id.Id_nota_Item_I);
//        Uid_Usuario_Item = mView.findViewById(R.id.Uid_Usuario_Item_I);
//        Correo_usuario_Item = mView.findViewById(R.id.Correo_usuario_Item_I);
//        Fecha_hora_registro_Item = mView.findViewById(R.id.Fecha_hora_registro_Item_I);
//        Titulo_Item = mView.findViewById(R.id.Titulo_Item_I);
//        Descripcion_Item = mView.findViewById(R.id.Descripcion_Item_I);
//        Fecha_Item = mView.findViewById(R.id.Fecha_Item_I);
//        Estado_Item = mView.findViewById(R.id.Estado_Item_I);

//    public void SetearDatos(Context context, String id_nota, String uid_usuario, String correo_usuario,
//                            String fecha_hora_registro, String titulo, String descripcion,
//                            String fecha_nota, String estado){
//        //Declarar las vistas
//        TextView Id_nota_Item, Correo,Uid_Usuario_Item,Correo_Usuario_Item,Fecha_Hora_Registro_Item,Titulo_Item,
//                Descripcion_Item, Fecha_Item, Estado_Item;
//        //Conexion con Item
//        Id_nota_Item = mView.findViewById(R.id.Id_nota_Item_I);
//        Uid_Usuario_Item = mView.findViewById(R.id.Uid_Usuario_Item_I);
//        Correo_Usuario_Item = mView.findViewById(R.id.Correo_usuario_Item_I);
//        Fecha_Hora_Registro_Item = mView.findViewById(R.id.Fecha_hora_registro_Item_I);
//        Titulo_Item = mView.findViewById(R.id.Titulo_Item_I);
//        Descripcion_Item = mView.findViewById(R.id.Descripcion_Item_I);
//        Fecha_Item = mView.findViewById(R.id.Fecha_Item_I);
//        Estado_Item = mView.findViewById(R.id.Estado_Item_I);
//
//        //Setear la info dentro del Item
//
//        Id_nota_Item.setText(id_nota);
//        Uid_Usuario_Item.setText(uid_usuario);
//        Correo_Usuario_Item.setText(correo_usuario);
//        Fecha_Hora_Registro_Item.setText(fecha_hora_registro);
//        Titulo_Item.setText(titulo);
//        Descripcion_Item.setText(descripcion);
//        Fecha_Item.setText(fecha_nota);
//        Estado_Item.setText(estado);

//        //SETEAR LA INFORMACIÓN DENTRO DEL ITEM

//
//
//    }
//
//}
