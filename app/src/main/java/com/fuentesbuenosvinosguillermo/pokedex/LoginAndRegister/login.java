package com.fuentesbuenosvinosguillermo.pokedex.LoginAndRegister;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.ActivityLoginBinding;

import java.util.Arrays;
import java.util.List;
/**
 * Clase encargada de realizar el login
 * */
public class login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    /**
     * `ActivityResultLauncher<Intent>` es una clase utilizada para iniciar actividades externas y manejar su resultado.
     * En este caso, se usa para iniciar el proceso de inicio de sesión con Firebase Authentication.
     *
     * - `signInLauncher`: Es un lanzador que gestiona la llamada a la actividad de inicio de sesión.
     * - `ActivityResultContracts.StartActivityForResult`: Define el contrato que especifica que se espera
     *    un resultado de la actividad lanzada.
     * - `result`: Contiene el resultado de la actividad, incluyendo un código que indica éxito o fracaso.
     *
     * Este enfoque moderno reemplaza al método obsoleto `startActivityForResult()`, proporcionando un manejo
     * más seguro del ciclo de vida de las actividades y evitando posibles problemas con la pérdida del contexto
     * en eventos como la rotación de pantalla.
     *
     * Cuando se lanza el `Intent` para iniciar sesión, `signInLauncher` espera el resultado, y una vez que la
     * actividad finaliza, se ejecuta la callback lambda proporcionada para manejar dicho resultado.
     */

    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Configurar el lanzador para inicio de sesión
        setupSignInLauncher();

// Configuración del ícono escalado
        Drawable googleIcon = getResources().getDrawable(R.drawable.googleicon);
        if (googleIcon != null) {
            // Escalar el ícono
            int width = 64;  // Ancho en píxeles
            int height = 64; // Alto en píxeles
            googleIcon.setBounds(0, 0, width, height);
            binding.iniciarGoogle.setCompoundDrawables(googleIcon, null, null, null);
        }

       // Opcional: Ajusta el padding entre el texto y el ícono
        binding.iniciarGoogle.setCompoundDrawablePadding(16); // Espaciado de 16dp

        binding.iniciarGoogle.setOnClickListener(v -> signInWithGoogle());


        Drawable iniciarSesion = getResources().getDrawable(R.drawable.login);
        if (iniciarSesion != null) {
            // Escalar el ícono
            int width = 64;  // Ancho en píxeles
            int height = 64; // Alto en píxeles
            iniciarSesion.setBounds(0, 0, width, height);
            binding.iniciarSesion.setCompoundDrawables(iniciarSesion, null, null, null);
        }

        // Opcional: Ajusta el padding entre el texto y el ícono
        binding.iniciarSesion.setCompoundDrawablePadding(16); // Espaciado de 16dp

        binding.iniciarSesion.setOnClickListener(v -> signInWithEmailPassword());

        Drawable register = getResources().getDrawable(R.drawable.email);
        if (register != null) {
            // Escalar el ícono
            int width = 64;  // Ancho en píxeles
            int height = 64; // Alto en píxeles
            register.setBounds(0, 0, width, height);
            binding.registrarse.setCompoundDrawables(register, null, null, null);
        }

       // Opcional: Ajusta el padding entre el texto y el ícono
        binding.registrarse.setCompoundDrawablePadding(16); // Espaciado de 16dp
        binding.registrarse.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, registro.class);
            startActivity(intent);
        });



    }

    // Método que se encarga de la configuracion necesaria para el login
    private void setupSignInLauncher() {
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        /**
                         * `IdpResponse` es una clase proporcionada por Firebase Authentication que encapsula la respuesta
                         * del proceso de autenticación realizado mediante el FirebaseUI. Contiene información sobre el
                         * resultado del inicio de sesión, como el estado de éxito, los datos del usuario o los errores que
                         * puedan haber ocurrido durante el proceso.

                         * - `fromResultIntent(Intent data)`: Es un método estático que extrae la respuesta del Intent devuelto
                         *    por la actividad de inicio de sesión. Permite acceder a los detalles del resultado de autenticación.
                         *
                         * En esta línea:
                         * `IdpResponse response = IdpResponse.fromResultIntent(result.getData());`
                         * - `result.getData()`: Recupera el `Intent` que contiene los datos devueltos por la actividad.
                         * - `response`: Es una instancia de `IdpResponse` que encapsula la información del resultado de inicio
                         *    de sesión, incluyendo si fue exitoso o si hubo algún error.
                         *
                         * Esta clase es útil para:
                         * - Obtener detalles del usuario autenticado.
                         * - Manejar errores específicos de inicio de sesión.
                         * - Verificar si el usuario canceló el proceso.
                         */

                        IdpResponse response = IdpResponse.fromResultIntent(result.getData());
                        if (response != null) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                Toast.makeText(this, "¡Bienvenido, " + user.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Inicio de sesión cancelado o fallido", Toast.LENGTH_SHORT).show();
                        if (result.getData() != null) {
                            IdpResponse response = IdpResponse.fromResultIntent(result.getData());
                            if (response != null && response.getError() != null) {
                                Log.e("Login", "Error: " + response.getError());
                            }
                        }
                    }
                }
        );
    }

    private void signInWithGoogle() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)

                .build();

        signInLauncher.launch(signInIntent);
    }

    private void signInWithEmailPassword() {
        EditText emailTxt = binding.emailLogin;
        EditText pass = binding.introducePass;
        String email = emailTxt.getText().toString().trim();
        String password = pass.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "¡Bienvenido, " + user.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Error de inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
