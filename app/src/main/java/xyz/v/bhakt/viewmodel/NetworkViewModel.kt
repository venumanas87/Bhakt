package xyz.v.bhakt.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import xyz.v.bhakt.models.Gita
import xyz.v.bhakt.models.Song
import xyz.v.bhakt.utils.Resource
import kotlin.random.Random

class NetworkViewModel: ViewModel() {
    private val db = Firebase.firestore
    var quotesLiveData:MutableLiveData<Resource<Gita>> = MutableLiveData()
    var ganeshSongLiveData:MutableLiveData<Resource<List<Song>>> = MutableLiveData()


    fun fetchQuote(){
        quotesLiveData.postValue(Resource.Loading())
        println("venu fetch")
        db.collection("data").get().addOnSuccessListener { doc->
            if (doc!=null) {
                val rand = Random.nextInt(doc.size())
                val d = doc.toList()[rand]
                val gita = d.toObject<Gita>()
                quotesLiveData.postValue(Resource.Success(data = gita))
                println("venuu data posted ${gita.quote} ${doc.size()}")
            }else
                println("empty doc venu")
        }.addOnFailureListener {
            quotesLiveData.postValue(Resource.Error(message = it.message.toString()))
            println("venu" + it.message)
        }
    }

    fun fetchGaneshSong(){
        ganeshSongLiveData.postValue(Resource.Loading())
        db.collection("ganesha_songs").get().addOnSuccessListener { doc->
            if (doc!=null) {
                val d = doc.toObjects<Song>()
                ganeshSongLiveData.postValue(Resource.Success(data = d))
                println("venuu data posted ${d[0].name}")
            }else
                println("empty doc venu gnesha")
        }.addOnFailureListener {
            ganeshSongLiveData.postValue(Resource.Error(message = it.message.toString()))
            println("venu" + it.message)
        }
    }

}