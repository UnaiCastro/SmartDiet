package com.tfg.smartdiet.iu.PaginaPrincipal.Info


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.tfg.smartdiet.R
import com.tfg.smartdiet.domain.ConfigUsuario
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


import com.tfg.smartdiet.iu.PaginaPrincipal.Historico.HistoricoActivity


class InfoFragment : Fragment() {
    private companion object {
        private const val CAMERA_REQUEST = 100
        private const val STORAGE_REQUEST = 200
        private const val IMAGEPICK_GALLERY_REQUEST = 300
        private const val IMAGE_PICKCAMERA_REQUEST = 400
    }
    private lateinit var cameraPermission: Array<String>
    private lateinit var set: ImageView
    private lateinit var editImgPerfil: Button
    private lateinit var editNom: Button
    private lateinit var editCont: Button
    private lateinit var pd: ProgressDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nombre: TextView
    private lateinit var correoUsuario: TextView
    private lateinit var editCorreo: Button
    private lateinit var storage:StorageReference
    private lateinit var cambiarTema: SwitchCompat
    private val pickImageLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data!!.data?.let { handleImageResult(it) }
        }
    }
    private val takePictureLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val bundle = result.data!!.extras
            val laminiatura =
                bundle!!["data"] as Bitmap?
            //GUARDAR COMO FICHERO
// Memoria externa
            val eldirectorio: File? =
                this.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val nombrefichero = "IMG_" + timeStamp + "_" + FirebaseAuth.getInstance()
                .currentUser?.uid
            val imagenFich =
                File(eldirectorio, "$nombrefichero.jpg")
            val os: OutputStream
            try {
                set =
                    view?.findViewById<ImageView>(R.id.imgPerfilInfo)!!
                //set.setImageBitmap(laminiatura)
                Picasso.get().load(imagenFich).
                fit().centerCrop().
                into(set)
                if (laminiatura != null) {
                    subirFoto(imagenFich.toUri(),laminiatura, "$nombrefichero.jpg")
                }
            } catch (e: Exception) {
                Toast.makeText(this.context, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        editNom = view.findViewById(R.id.editarNombreInfo)
        editImgPerfil = view.findViewById(R.id.actuPerfilInfo)
        editCorreo = view.findViewById(R.id.editarMailInfo)
        editCont = view.findViewById(R.id.cambiarContrInfo)
        cambiarTema = view.findViewById(R.id.temaInfo)
        setNombre(view)
        setCorreo(view)
        setFoto()
        val conf =
            this.context?.let { ConfigUsuario(it.getSharedPreferences("Configuracion", Context.MODE_PRIVATE)) }
        if (conf != null) {
            if (conf.initTema()=="OSCURO"){
                cambiarTema.setChecked(true)
            }else{
                cambiarTema.setChecked(false)
            }
        }
        cambiarTema.setOnClickListener{
            if (!cambiarTema.isChecked){
                conf?.setTema("NORMAL")
            }else{
                conf?.setTema("OSCURO")
            }
        }

        pd = ProgressDialog(this.context)
        editNom.setOnClickListener{
            editarNom(view)
        }
        editCorreo.setOnClickListener{
            editarMail(view)
        }
        editImgPerfil.setOnClickListener{
            editarFoto()
        }
        editCont.setOnClickListener{
            editarCont()
        }

        val btn = view.findViewById<Button>(R.id.BTNHistorico)
        btn.setOnClickListener {
            val i= Intent(context,HistoricoActivity::class.java)
            startActivity(i)
        }

        val btnGD = view.findViewById<Button>(R.id.BTNgestiondietas)
        btnGD.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.Main_fragmentcontainerview)
            navController.navigate(R.id.gestionDietasFragment)
        }

    }


    //Editar nombre
    private fun editarNom(vista: View) {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Cambiar nombre") //cambiar por strings
        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        val editText = EditText(this.context)
        editText.hint = "Actualiza tu nombre"
        layout.addView(editText)
        builder.setView(layout)
        builder.setPositiveButton("Actualizar") { dialog, _ ->
            val value = editText.text.toString().trim()
            if (value.isNotEmpty()) {
                pd.show()

                // Here we are updating the new name
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val userRef = db.collection("users").document(userId)
                    val nuevoNombre = hashMapOf(
                        "nombreUsuario" to value
                    )
                    userRef.update(nuevoNombre as Map<String, Any>).addOnSuccessListener {
                        pd.dismiss()
                        Toast.makeText(this.context, " Actualizado ", Toast.LENGTH_LONG).show()
                        setNombre(vista)
                    }
                    .addOnFailureListener {
                        pd.dismiss()
                        Toast.makeText(this.context, "Error", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
        builder.show()
    }

    private fun setNombre(vista: View){
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser
        if (userId != null) {
            val userRef = db.collection("users").document(userId.uid)
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Obtener el valor actual del campo "nombreUsuario"
                        val nombreUsuario = documentSnapshot.getString("nombreUsuario")
                        if (nombreUsuario != null) {
                           nombre = vista.findViewById(R.id.nombreInfo)
                           nombre.text = nombreUsuario
                            Log.d("TAG", "El nombre de usuario es: $nombreUsuario")
                        } else {
                            Log.d("TAG", "No se encontró el campo 'nombreUsuario'")
                        }
                    } else {
                        Log.d("TAG", "No se encontró el documento del usuario")
                    }
                }
                .addOnFailureListener { e ->
                    // Error al obtener el documento
                    Log.w("TAG", "Error getting document", e)
                }

        }
    }

    private fun setCorreo(vista: View){
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser
        if (userId != null) {
            val userRef = db.collection("users").document(userId.uid)
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Obtener el valor actual del campo "nombreUsuario"
                        val correo = documentSnapshot.getString("correo")
                        if (correo != null) {
                            correoUsuario = vista.findViewById(R.id.correoInfo)
                            correoUsuario.text = correo
                            Log.d("TAG", "El correo del usuario es: $correo")
                        } else {
                            Log.d("TAG", "No se encontró el campo 'nombreUsuario'")
                        }
                    } else {
                        Log.d("TAG", "No se encontró el documento del usuario")
                    }
                }
                .addOnFailureListener { e ->
                    // Error al obtener el documento
                    Log.w("TAG", "Error getting document", e)
                }

        }
    }

    private fun editarMail(vista: View) {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Cambiar correo") //cambiar por strings
        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        val editText = EditText(this.context)
        editText.hint = "Actualiza tu correo"
        layout.addView(editText)
        builder.setView(layout)
        builder.setPositiveButton("Actualizar") { dialog, _ ->
            val value = editText.text.toString().trim()
            val emailRegex = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
            if (!emailRegex.matches(value)) {
                Toast.makeText(this.context, "Introduce un correo válido", Toast.LENGTH_LONG).show()
            }else if(value.isNotEmpty()) {
                pd.show()

                // Here we are updating the new name
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val userRef = db.collection("users").document(userId)
                    val nuevoCorreo = hashMapOf(
                        "correo" to value
                    )
                    //auth.currentUser?.verifyBeforeUpdateEmail(value)
                    userRef.update(nuevoCorreo as Map<String, Any>).addOnSuccessListener {
                        pd.dismiss()
                        Toast.makeText(this.context, " Actualizado ", Toast.LENGTH_LONG).show()
                        setCorreo(vista)
                    }
                        .addOnFailureListener {
                            pd.dismiss()
                            Toast.makeText(this.context, "Error", Toast.LENGTH_LONG).show()
                        }
                }

            }
        }
        builder.show()
    }

    private fun editarCont() {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Cambiar contraseña") //cambiar por strings
        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        val editContActu = EditText(this.context)
        editContActu.hint = "Introduce tu contraseña actual"
        layout.addView(editContActu)
        val editCon = EditText(this.context)
        editCon.hint = "Introduce una nueva contraseña"
        layout.addView(editCon)
        val editCon2 = EditText(this.context)
        editCon2.hint = "Confirma la contraseña"
        layout.addView(editCon2)
        builder.setView(layout)
        builder.setPositiveButton("Actualizar") { dialog, _ ->
            val antCont = editContActu.text.toString().trim()
            val newCont = editCon.text.toString().trim()
            val newCont2 = editCon2.text.toString().trim()
            if(newCont.length>=6 && newCont == newCont2) {
                pd.show()
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                val authCredential =
                    EmailAuthProvider.getCredential(auth.currentUser?.email!!, antCont)
                // Here we are updating the new name
                val userId = auth.currentUser?.uid
                val user = auth.currentUser
                if (userId != null && user != null) {
                    // Reautenticar al usuario antes de actualizar la contraseña
                    user.reauthenticate(authCredential)
                        .addOnSuccessListener {
                            // Actualizar la contraseña
                            user.updatePassword(newCont)
                                .addOnSuccessListener {
                                    pd.dismiss()
                                    Toast.makeText(
                                        this.context,
                                        "Se ha cambiado la contraseña",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }.addOnFailureListener { e ->
                                    pd.dismiss()
                                    Log.e("TAG", "Error al actualizar la contraseña: ${e.message}", e)
                                    Toast.makeText(
                                        this.context,
                                        "Error al actualizar la contraseña: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }.addOnFailureListener { e ->
                            pd.dismiss()
                            Log.e("TAG", "Error al reautenticar al usuario: ${e.message}", e)
                            Toast.makeText(
                                this.context,
                                "Error al reautenticar al usuario: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {
                    // La credencial de autenticación es nula
                    pd.dismiss()
                    Toast.makeText(
                        this.context,
                        "Error: Credencial de autenticación nula",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }else{
                pd.dismiss()
                Toast.makeText(this.context, "La contraseña debe tener 6 caracteres como mínimo y coincidir", Toast.LENGTH_LONG).show()
            }
        }
        builder.show()
    }

    private fun editarFoto(){
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle("Actualizar imagen de perfil")
        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        builder.setView(layout)
        builder.setItems(
            arrayOf<CharSequence>(
                "Galería",
                "Cámara"
            )
        ) { dialog: DialogInterface?, which: Int ->
            when (which) {
                0 -> {
                    // Abrir galería
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    galleryIntent.setType("image/*")
                    pickImageLauncher.launch(galleryIntent)
                }

                1 -> {
                    try {
                        if (!checkCameraPermission()) {
                            requestCameraPermission()
                        }
                        // Abrir cámara
                        val cameraIntent =
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureLauncher.launch(cameraIntent)
                    }catch(e:Exception){
                        Toast.makeText(this.context, "No tienes permisos para usar la cámara", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        builder.show()
    }

    private fun handleImageResult(selectedImageUri: Uri) {
        try {
            // Obtener la InputStream de la imagen seleccionada
            val inputStream: InputStream? =
                this.context?.contentResolver?.openInputStream(selectedImageUri)
            var nombreArchivo: String? = null
            // Decodificar la InputStream en un Bitmap
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val nombrefichero = "IMG_" + timeStamp + "_" + FirebaseAuth.getInstance()
                .currentUser?.uid
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Mostrar el Bitmap en un ImageView
            //view?.findViewById<ImageView>(R.id.imgPerfilInfo)?.setImageBitmap(bitmap)
            Picasso.get().load(selectedImageUri.toString()).fit().into(view?.findViewById<ImageView>(R.id.imgPerfilInfo))
            subirFoto(selectedImageUri,bitmap, "$nombrefichero.jpg")
            // Cerrar la InputStream
            inputStream?.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    // Comprobar si los permisos de la cámara están activados
    private fun checkCameraPermission(): Boolean {
        val result = this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } == PackageManager.PERMISSION_GRANTED
        val result1 = this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    // Pedir que se actitven los permisos de la cámara
    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST)
    }

    private fun subirFoto(uri: Uri, bitmap:Bitmap, nomFoto:String){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        //val nuevoNomFoto = nomFoto.split(".")[0]

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        //val data = baos.toByteArray()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid!!
        val perfilRef = storageRef.child("perfil/$nomFoto")

        // Subir la imagen al almacenamiento de Firebase Storage
        val uploadTask = perfilRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Guardar la URL de descarga en el documento del usuario en Firestore
                val imageUrl = uri.toString()

                val userRef = db.collection("users").document(userId)
                userRef.update("foto", imageUrl)
                    .addOnSuccessListener {
                        Log.d("TAG", "URL de imagen actualizada exitosamente en Firestore")
                        Toast.makeText(this.context, " Actualizado ", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this.context, "Error", Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this.context, "Error", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            // Obtener la URL de descarga de la imagen subida
            Toast.makeText(this.context, "Error", Toast.LENGTH_LONG).show()
        }
    }



    private fun setFoto(){
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser
        if (userId != null) {
            val userRef = db.collection("users").document(userId.uid)
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Obtener el valor actual del campo "nombreUsuario"
                        val foto = documentSnapshot.getString("foto")
                        if (foto != null) {
                            set = view?.findViewById<ImageView>(R.id.imgPerfilInfo)!!
                            Picasso.get().load(foto.toString()).fit().into(view?.findViewById<ImageView>(R.id.imgPerfilInfo))
                            Log.d("TAG", "La foto del usuario es: $foto")
                        } else {

                            set = view?.findViewById<ImageView>(R.id.imgPerfilInfo)!!
                            Picasso.get().load(R.drawable.default_profile_picture_grey_male_icon).
                            fit().centerCrop().
                            into(set)
                            Log.d("TAG", "No se encontró el campo 'foto'")
                        }
                    } else {
                        Log.d("TAG", "No se encontró el documento del usuario")
                    }
                }
                .addOnFailureListener { e ->
                    // Error al obtener el documento
                    Log.w("TAG", "Error getting document", e)
                }

        }
    }

    private fun cambiarTema(tema:String){
        val conf =
            this.context?.let { ConfigUsuario(it.getSharedPreferences("Configuracion", Context.MODE_PRIVATE)) }
        if (conf != null) {
            conf.setTema(tema)
        }

    }
}
