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
import android.widget.RadioButton
import android.widget.RadioGroup
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
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.tfg.smartdiet.R
import com.tfg.smartdiet.domain.ConfigUsuario
import com.tfg.smartdiet.iu.Bienvenida.BienvenidaActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.tfg.smartdiet.iu.PaginaPrincipal.Historico.HistoricoActivity

class InfoFragment : Fragment() {
    private companion object {
        private const val CAMERA_REQUEST = 100
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
    //private lateinit var editCorreo: Button
    private lateinit var cambiarTema: SwitchCompat
    private lateinit var desNotis: SwitchCompat
    private lateinit var editIdioma: Button
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
                // Crear un archivo temporal en el directorio de almacenamiento


            try {
                set =
                    view?.findViewById<ImageView>(R.id.imgPerfilInfo)!!
                set.setImageBitmap(laminiatura)
                //Picasso.get().load(imagenFich).
                //fit().centerCrop().
                //into(set)
                if (laminiatura != null) {
                    subirFoto(imagenFich.toUri(),laminiatura, "$nombrefichero.jpg",true)
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
       // editCorreo = view.findViewById(R.id.editarMailInfo)
        editCont = view.findViewById(R.id.cambiarContrInfo)
        cambiarTema = view.findViewById(R.id.temaInfo)
        editIdioma = view.findViewById(R.id.idiomaInfo)
        desNotis = view.findViewById(R.id.notisInfo)
        setNombre(view)
        setCorreo(view)
        setFoto()
        val conf =
            this.context?.let { ConfigUsuario(it.getSharedPreferences("Configuracion", Context.MODE_PRIVATE)) }
        if (conf != null) {
            conf.initTema()
            context?.let { conf.initIdioma(it) }
        }

        if (conf != null) {
            if (conf.initTema()=="OSCURO"){
                cambiarTema.isChecked = true
            }else{
                cambiarTema.isChecked = false
            }
        }
        if (conf != null) {
            desNotis.isChecked = conf.getNotis()=="ON"
        }

        desNotis.setOnClickListener{
            if (conf != null) {
                if (desNotis.isChecked) {
                    desactivarNotis("ON", conf)
                }else{
                    desactivarNotis("OFF", conf)
                }
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
       /* editCorreo.setOnClickListener{
            editarMail(view)
        }*/
        editImgPerfil.setOnClickListener{
            editarFoto()
        }
        editCont.setOnClickListener{
            editarCont()
        }
        editIdioma.setOnClickListener{
            if (conf != null) {
                cambiarIdioma(conf)
            }
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

        val btnCerrarSesion = view.findViewById<Button>(R.id.BTNlogout)
        btnCerrarSesion.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            auth.signOut()
            val i= Intent(context,BienvenidaActivity::class.java)
            startActivity(i)
            conf?.cerrarSesion()
        }

    }


    //Editar nombre
    private fun editarNom(vista: View) {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle(resources.getString(R.string.str_cambiar_nom)) //cambiar por strings
        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        val editText = EditText(this.context)
        editText.hint = resources.getString(R.string.str_actu_nom)
        layout.addView(editText)
        builder.setView(layout)
        builder.setPositiveButton(resources.getString(R.string.str_actualizar)) { dialog, _ ->
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
                        Toast.makeText(this.context, resources.getString(R.string.str_actualizado), Toast.LENGTH_LONG).show()
                        setNombre(vista)
                    }
                    .addOnFailureListener {
                        pd.dismiss()
                        Toast.makeText(this.context, resources.getString(R.string.str_error), Toast.LENGTH_LONG).show()
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
                    Log.e("TAG", "Error getting document", e)
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

    private fun editarCont() {
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle(resources.getString(R.string.str_camb_cont)) //cambiar por strings
        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        val editContActu = EditText(this.context)
        editContActu.hint = resources.getString(R.string.str_intro_cont_actu)
        layout.addView(editContActu)
        val editCon = EditText(this.context)
        editCon.hint = resources.getString(R.string.str_introduce_nueva_cont)
        layout.addView(editCon)
        val editCon2 = EditText(this.context)
        editCon2.hint = resources.getString(R.string.str_confirm_contra)
        layout.addView(editCon2)
        builder.setView(layout)
        builder.setPositiveButton(resources.getString(R.string.str_actualizar)) { dialog, _ ->
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
                                        resources.getString(R.string.str_toast_camb_cont),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }.addOnFailureListener { e ->
                                    pd.dismiss()
                                    Log.e("TAG", "Error al actualizar la contraseña: ${e.message}", e)
                                    Toast.makeText(
                                        this.context,
                                        resources.getString(R.string.str_error),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }.addOnFailureListener { e ->
                            pd.dismiss()
                            Log.e("TAG", "Error al reautenticar al usuario: ${e.message}", e)
                            Toast.makeText(
                                this.context,
                                resources.getString(R.string.str_error),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {
                    // La credencial de autenticación es nula
                    pd.dismiss()
                    Toast.makeText(
                        this.context,
                        resources.getString(R.string.str_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }else{
                pd.dismiss()
                Toast.makeText(this.context, resources.getString(R.string.str_toast_cont_seis), Toast.LENGTH_LONG).show()
            }
        }
        builder.show()
    }

    private fun editarFoto(){
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle(resources.getString(R.string.str_actu_perfil))
        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        builder.setView(layout)
        builder.setItems(
            arrayOf<CharSequence>(
                resources.getString(R.string.str_galeria),
                resources.getString(R.string.str_camara)
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
                        Toast.makeText(this.context, resources.getString(R.string.str_permiso_camara), Toast.LENGTH_LONG).show()
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
            //var nombreArchivo: String? = null
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
            subirFoto(selectedImageUri,bitmap, "$nombrefichero.jpg",false)
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

    private fun subirFoto(uri: Uri, bitmap:Bitmap, nomFoto:String, camara:Boolean){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        //val nuevoNomFoto = nomFoto.split(".")[0]

        //val baos = ByteArrayOutputStream()
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        //val data = baos.toByteArray()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid!!
        //val perfilRef = storageRef.child("perfil/$nomFoto")
        val perfilRef = storageRef.child("perfil/$nomFoto")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        // Subir la imagen al almacenamiento de Firebase Storage
        val uploadTask: UploadTask = if(!camara) {
            perfilRef.putFile(uri)
        }else{
            perfilRef.putBytes(data)
        }
        uploadTask.addOnSuccessListener {
            perfilRef.downloadUrl.addOnSuccessListener { uri ->
                // Guardar la URL de descarga en el documento del usuario en Firestore
                val imageUrl = "perfil/$nomFoto"

                val userRef = db.collection("users").document(userId)
                userRef.update("foto", imageUrl)
                    .addOnSuccessListener {
                        Log.d("TAG", "URL de imagen actualizada exitosamente en Firestore")
                        Toast.makeText(this.context, resources.getString(R.string.str_actualizado), Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this.context, resources.getString(R.string.str_error), Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(this.context, resources.getString(R.string.str_error), Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            // Obtener la URL de descarga de la imagen subida
            Toast.makeText(this.context, resources.getString(R.string.str_error), Toast.LENGTH_LONG).show()
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
                            val storage = FirebaseStorage.getInstance()
                            val storageRef = storage.reference
                            val pathReference = storageRef.child(foto) // 'foto' es la ruta de tu imagen en Firebase Storage

// Obtén la URL de descarga
                            pathReference.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                // Encuentra tu ImageView
                                val imageView: ImageView = view?.findViewById(R.id.imgPerfilInfo)!!
                                // Usa Picasso para cargar la imagen desde la URL
                                Picasso.get().load(imageUrl).fit().into(imageView)
                                Log.d("TAG", "La foto del usuario es: $imageUrl")
                            }.addOnFailureListener {
                                Log.d("TAG", "No se ha encontrado: $foto")
                            }

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

    private fun cambiarIdioma(conf:ConfigUsuario){
        val idiomaInit = this.context?.let { conf.initIdioma(it) }
        val builder = AlertDialog.Builder(this.context)
        builder.setTitle(resources.getString(R.string.str_idioma)) //cambiar por strings
        val layout = LinearLayout(this.context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)
        val grupo = RadioGroup(this.context)
        val espanol = RadioButton(this.context)
        espanol.text = resources.getString(R.string.str_es)
        val ingles = RadioButton(this.context)
        ingles.text = resources.getString(R.string.str_en)
        grupo.addView(espanol)
        grupo.addView(ingles)
        layout.addView(grupo)
        builder.setView(layout)
        if(idiomaInit=="es"){
            grupo.check(espanol.id)
        }else{
            grupo.check(ingles.id)
        }
        builder.setPositiveButton(resources.getString(R.string.str_actualizar)) { dialog, _ ->
            if(espanol.isChecked){
                conf.setIdioma("es",this.requireContext())
            }else{
                conf.setIdioma("en",this.requireContext())
            }
            val intent = requireActivity().intent
            requireActivity().finish()
            requireActivity().startActivity(intent)
        }
        builder.show()
    }

    private fun desactivarNotis(noti: String, conf:ConfigUsuario){
        conf.setNotis(noti)
    }

}
