package pl.edu.pjatk.dziejesie

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.edu.pjatk.dziejesie.databinding.RegisterViewBinding

class RegisterView : AppCompatActivity() {

    private lateinit var binding: RegisterViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRegisterBtn()
    }

    private fun setupRegisterBtn() {
        binding.registerBtn.setOnClickListener {
            if(validate()){
                ServiceLocator.auth.createUserWithEmailAndPassword(binding.registerLogin.text.toString(),binding.registerPassword.text.toString())
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(
                                this@RegisterView,
                                "Zarejestrowano!",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }else{
                            Toast.makeText(
                                this@RegisterView,
                                "Wystąpił błąd, spróbuj ponownie :(",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

        }
    }

    private fun validate(): Boolean {
        var isValid = true;
        if(binding.registerLogin.text.toString() == ""){
            binding.registerLogin.setError("Pole wymagane")
            isValid = false
        }else{
            binding.registerLogin.setError(null)
        }
        if(binding.registerPassword.text.toString() == ""){
            binding.registerPassword.setError("Pole wymagane")
            isValid = false
        }else{
            binding.registerPassword.setError(null)
        }
        if(binding.registerPassword2.text.toString() == ""){
            binding.registerPassword2.setError("Pole wymagane")
            isValid = false
        }else{
            binding.registerPassword2.setError(null)
        }
        if(isValid && !binding.registerPassword2.text.toString().equals(binding.registerPassword.text.toString())){
            binding.registerPassword.setError("Hasła muszą być takie same")
            isValid = false
        }else{
            binding.registerPassword.setError(null)
        }
        return isValid
    }
}