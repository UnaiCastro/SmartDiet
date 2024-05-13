package com.tfg.smartdiet.iu.PaginaPrincipal.Info

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.R

class InfoFragment : Fragment() {
    private lateinit var set: ImageView
    private lateinit var editImgPerfil: Button
    private lateinit var editNom: Button
    private lateinit var editCont: Button
    private lateinit var pd: ProgressDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var nombre: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editNom = view.findViewById(R.id.editarNombreInfo)
        editImgPerfil = view.findViewById(R.id.actuPerfilInfo)
        setNombre(view)
        pd = ProgressDialog(this.context)
            editNom.setOnClickListener{
                editarNom(view)
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
        editText.hint = "Actualiza tu nombre: "
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
}

/*
    builder.setNegativeButton("Cancel") { dialog, _ ->
        pd.dismiss()
    }
    builder.create().show()
}
/*private lateinit var firebaseAuth: FirebaseAuth
private lateinit var firebaseUser: FirebaseUser
private lateinit var firebaseDatabase: FirebaseDatabase
private lateinit var databaseReference: DatabaseReference
private lateinit var storageReference: StorageReference
private val storagepath = "Users_Profile_Cover_image/"
private lateinit var uid: String
private lateinit var set: ImageView
private lateinit var profilepic: TextView
private lateinit var editname: TextView
private lateinit var editpassword: TextView
private lateinit var pd: ProgressDialog
private companion object {
    private const val CAMERA_REQUEST = 100
    private const val STORAGE_REQUEST = 200
    private const val IMAGEPICK_GALLERY_REQUEST = 300
    private const val IMAGE_PICKCAMERA_REQUEST = 400
}
private lateinit var cameraPermission: Array<String>
private lateinit var storagePermission: Array<String>
private var imageuri: Uri? = null
private var profileOrCoverPhoto: String? = null
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    // Inflate the layout for this fragment

    return inflater.inflate(R.layout.fragment_info, container, false)
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    profilepic = findViewById(R.id.profilepic)
    editname = findViewById(R.id.editname)
    set = findViewById(R.id.setting_profile_image)
    pd = ProgressDialog(this)
    pd.setCanceledOnTouchOutside(false)
    editpassword = findViewById(R.id.changepassword)
    firebaseAuth = FirebaseAuth.getInstance()
    firebaseUser = firebaseAuth.currentUser!!
    firebaseDatabase = FirebaseDatabase.getInstance()
    storageReference = FirebaseStorage.getInstance().reference
    databaseReference = firebaseDatabase.reference.child("Users")
    cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val query = databaseReference.orderByChild("email").equalTo(firebaseUser.email)
    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (dataSnapshot1 in dataSnapshot.children) {
                val image = "" + dataSnapshot1.child("image").value
                try {
                    Glide.with(this@EditProfilePage).load(image).into(set)
                } catch (e: Exception) {
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })

    editpassword.setOnClickListener {
        pd.setMessage("Changing Password")
        showPasswordChangeDailog()
    }

    profilepic.setOnClickListener {
        pd.setMessage("Updating Profile Picture")
        profileOrCoverPhoto = "image"
        showImagePicDialog()
    }

    editname.setOnClickListener {
        pd.setMessage("Updating Name")
        showNamephoneupdate("name")
    }
}
@Override
override fun onPause() {
    super.onPause()
    val query = databaseReference.orderByChild("email").equalTo(firebaseUser.email)
    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (dataSnapshot1 in dataSnapshot.children) {
                val image = "" + dataSnapshot1.child("image").value
                try {
                    Glide.with(this@InfoFragment).load(image).into(set)
                } catch (e: Exception) {
                }
            }
        }

        fun onCancelled(databaseError: DatabaseError) {}
    })

    editpassword.setOnClickListener {
        pd.setMessage("Changing Password")
        showPasswordChangeDailog()
    }
}

override fun onStart() {
    super.onStart()
    val query = databaseReference.orderByChild("email").equalTo(firebaseUser.email)
    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (dataSnapshot1 in dataSnapshot.children) {
                val image = "" + dataSnapshot1.child("image").value
                try {
                    Glide.with(this@InfoFragment).load(image).into(set)
                } catch (e: Exception) {
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
        }
    })
    editpassword.setOnClickListener {
        pd.setMessage("Changing Password")
        showPasswordChangeDailog()
    }
}

// checking storage permission ,if given then we can add something in our storage
private fun checkStoragePermission(): Boolean {
    val result = this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_GRANTED
    return result
}

// requesting for storage permission
private fun requestStoragePermission() {
    requestPermissions(storagePermission, STORAGE_REQUEST)
}

// checking camera permission ,if given then we can click image using our camera
private fun checkCameraPermission(): Boolean {
    val result = this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } == PackageManager.PERMISSION_GRANTED
    val result1 = this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_GRANTED
    return result && result1
}

// requesting for camera permission if not given
private fun requestCameraPermission() {
    requestPermissions(cameraPermission, CAMERA_REQUEST)
}

// We will show an alert box where we will write our old and new password
private fun showPasswordChangeDailog() {
    val view = LayoutInflater.from(this.context).inflate(R.layout.dialog_update_password, null)
    val oldpass = view.findViewById<EditText>(R.id.oldpasslog)
    val newpass = view.findViewById<EditText>(R.id.newpasslog)
    val editpass = view.findViewById<Button>(R.id.updatepass)
    val builder = AlertDialog.Builder(this.context)
    builder.setView(view)
    val dialog = builder.create()
    dialog.show()
    editpass.setOnClickListener {
        val oldp = oldpass.text.toString().trim()
        val newp = newpass.text.toString().trim()
        if (TextUtils.isEmpty(oldp)) {
            Toast.makeText(this@InfoFragment, "Current Password cant be empty", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }
        if (TextUtils.isEmpty(newp)) {
            Toast.makeText(this@InfoFragment, "New Password cant be empty", Toast.LENGTH_LONG).show()
            return@setOnClickListener
        }
        dialog.dismiss()
        updatePassword(oldp, newp)
    }
}

// Updating name
private fun showNamephoneupdate(key: String) {
    val builder = AlertDialog.Builder(this.context)
    builder.setTitle("Update $key")

    // creating a layout to write the new name
    val layout = LinearLayout(this.context)
    layout.orientation = LinearLayout.VERTICAL
    layout.setPadding(10, 10, 10, 10)
    val editText = EditText(this.context)
    editText.hint = "Enter $key"
    layout.addView(editText)
    builder.setView(layout)

    builder.setPositiveButton("Update") { dialog, _ ->
        val value = editText.text.toString().trim()
        if (value.isNotEmpty()) {
            pd.show()

            // Here we are updating the new name
            val result = hashMapOf(key to value)
            databaseReference.child(firebaseUser.uid).updateChildren(result)
                .addOnSuccessListener {
                    pd.dismiss()

                    // after updated we will show updated
                    Toast.makeText(this@InfoFragment, " updated ", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    pd.dismiss()
                    Toast.makeText(this@InfoFragment, "Unable to update", Toast.LENGTH_LONG).show()
                }

            if (key == "name") {
                val databaser = FirebaseDatabase.getInstance().reference("Posts")
                val query = databaser.orderByChild("uid").equalTo(uid)
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (dataSnapshot1 in dataSnapshot.children) {
                            val child = databaser.key
                            dataSnapshot1.ref.child("uname").setValue(value)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        } else {
            Toast.makeText(this@InfoFragment, "Unable to update", Toast.LENGTH_LONG).show()
        }
    }
*/
/*
    builder.setNegativeButton("Cancel") { dialog, _ ->
        pd.dismiss()
    }
    builder.create().show()
}

private fun showImagePicDialog() {
    val options = arrayOf("Camera", "Gallery")
    val builder = AlertDialog.Builder(this.context)
    builder.setTitle("Pick Image From")
    builder.setItems(options) { dialog, which ->
        // if access is not given then we will request for permission
        if (which == 0) {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                pickFromCamera()
            }
        } else if (which == 1) {
            if (!checkStoragePermission()) {
                requestStoragePermission()
            } else {
                pickFromGallery()
            }
        }
    }
    builder.create().show()
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
            imageuri = data?.data
            uploadProfileCoverPhoto(imageuri)
        }
        if (requestCode == IMAGE_PICKCAMERA_REQUEST) {
            uploadProfileCoverPhoto(imageuri)
        }
    }
}

override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    when (requestCode) {
        CAMERA_REQUEST -> {
            if (grantResults.isNotEmpty()) {
                val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted && writeStorageAccepted) {
                    pickFromCamera()
                } else {
                    Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show()
                }
            }
        }
        STORAGE_REQUEST -> {
            if (grantResults.isNotEmpty()) {
                val writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (writeStorageAccepted) {
                    pickFromGallery()
                } else {
                    Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
*/
/*
// Here we will click a photo and then go to startactivityforresult for updating data
private fun pickFromCamera() {
    val contentValues = ContentValues()
    contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic")
    contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
    imageuri = this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    val camerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri)
    startActivityForResult(camerIntent, IMAGE_PICKCAMERA_REQUEST)
}
*/
// We will select an image from gallery
/*    private fun pickFromGallery() {
    val galleryIntent = Intent(Intent.ACTION_PICK)
    galleryIntent.type = "image/*"
    startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST)
}
*/
/*
 */
// We will upload the image from here.
private fun uploadProfileCoverPhoto(uri: Uri) {
    pd.show()

    // We are taking the filepath as storagepath + firebaseauth.getUid()+".png"
    val filepathname = storagepath + "" + profileOrCoverPhoto + "_" + firebaseUser.uid
    val storageReference1: StorageReference = storageReference.child(filepathname)
    storageReference1.putFile(uri).addOnSuccessListener(OnSuccessListener<Any> { taskSnapshot ->
        val uriTask: Task<Uri> = taskSnapshot.getStorage().getDownloadUrl()
        while (!uriTask.isSuccessful);

        // We will get the url of our image using uritask
        val downloadUri = uriTask.result
        if (uriTask.isSuccessful) {

            // updating our image url into the realtime database
            val hashMap = HashMap<String, Any>()
            hashMap[profileOrCoverPhoto!!] = downloadUri.toString()
            databaseReference.child(firebaseUser.uid).updateChildren(hashMap)
                .addOnSuccessListener(
                    OnSuccessListener<Void?> {
                        pd.dismiss()
                        Toast.makeText(this@InfoFragment, "Updated", Toast.LENGTH_LONG)
                            .show()
                    }).addOnFailureListener(OnFailureListener {
                    pd.dismiss()
                    Toast.makeText(this@InfoFragment, "Error Updating ", Toast.LENGTH_LONG)
                        .show()
                })
        } else {
            pd.dismiss()
            Toast.makeText(this.context, "Error", Toast.LENGTH_LONG).show()
        }
    }).addOnFailureListener(OnFailureListener {
        pd.dismiss()
        Toast.makeText(this.context, "Error", Toast.LENGTH_LONG).show()
    })
}*/