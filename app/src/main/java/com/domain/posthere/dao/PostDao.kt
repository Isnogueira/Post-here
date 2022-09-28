package com.domain.posthere.dao

import com.domain.posthere.model.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostDao {
    private val collection = "posts"
    private val db = Firebase.firestore

    fun salvar(post: Post): Task<Void>? {
        var task: Task<Void>? = null
        if(post.id == null){
            val ref: DocumentReference = db.collection(collection).document()
            post.id = ref.id
            task = ref.set(post)
        }
        return task
    }

    fun listar(): Task<QuerySnapshot> {
        return db.collection(collection).get()
    }

    fun deletar(id: String): Task<Void> {
       return db.collection(collection).document(id).delete()
    }

}