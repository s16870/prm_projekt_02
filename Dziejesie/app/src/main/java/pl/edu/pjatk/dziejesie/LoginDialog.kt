package pl.edu.pjatk.dziejesie

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import pl.edu.pjatk.dziejesie.databinding.LoginDialogBinding

const val RC_SIGN_IN = 1
const val REGISTER_REQUEST = 2
class LoginView() : AppCompatActivity(){

    private lateinit var binding: LoginDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLoginBtn()
        setupGoogleLoginBtn()
        setupRegisterBtn()
    }

    private fun setupRegisterBtn() {
        binding.registerBtn.setOnClickListener {
            val addEventIntent = Intent(applicationContext, RegisterView::class.java)
            startActivityForResult(addEventIntent, REGISTER_REQUEST)
        }

    }

    private fun setupGoogleLoginBtn() {
        var googleBtn = binding.googleLoginButton as SignInButton
        googleBtn?.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            var googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                ServiceLocator.auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithCredential:success")
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            println(task.exception?.localizedMessage)
                        }
                    }.addOnFailureListener{
                            it.printStackTrace()
                        }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun setupLoginBtn() {
        binding.loginBtn.setOnClickListener {
            val loginInput = binding.login
            val passwordInput = binding.password
            val login = loginInput.text.toString()
            val password = passwordInput.text.toString()
            if(login == null || login == ""){
                loginInput.setError("Pole wymagane!")
            }else{
                loginInput.setError(null)
            }
            if(password == null){
                passwordInput.setError("Pole wymagane!")
            }else{
                passwordInput.setError(null)
            }
            if(login != null && password != null){
                ServiceLocator.auth.signInWithEmailAndPassword(login,password).addOnCompleteListener(
                    OnCompleteListener <AuthResult>{ task ->
                        if(task.isSuccessful){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }else{
                            Toast.makeText(
                                    applicationContext,
                                "Błędny login lub hasło",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                )
            }
        }
    }

}