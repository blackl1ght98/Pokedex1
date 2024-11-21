package com.fuentesbuenosvinosguillermo.pokedex.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.ActivityLoginBinding;

import java.util.Arrays;
import java.util.List;

public class login extends AppCompatActivity {

    // Clase para gestionar el enlace entre las vistas del diseño y el código
    private ActivityLoginBinding binding;
    // Instancia de Firebase Authentication para manejar la autenticación
    private FirebaseAuth mAuth;
    // Lanzador para manejar el resultado de la actividad de inicio de sesión

    /** Definicion:
    ActivityResultLauncher es una clase en Android que se utiliza para manejar resultados de actividades de una manera moderna y más segura
    que el método obsoleto onActivityResult.
    * */
    /**Caracteristicas:
     * Registro explícito de contratos: Define claramente qué actividad se inicia y qué tipo de resultado se espera.
     *Simplificación del código: Elimina la necesidad de verificar manualmente los códigos de solicitud (requestCode) en onActivityResult.
     * Compatibilidad: Funciona con las nuevas API de actividades y fragments, lo que permite un código más limpio y reutilizable.
     * */
    /**Como funciona ActivityResultLauncher:
     * Contrato de actividad: Define qué actividad quieres iniciar y cómo manejar el resultado. Esto se hace a través de un contrato, como el ActivityResultContracts.
     * Registro del lanzador: Se registra el lanzador en un ciclo de vida seguro (normalmente en onCreate) usando registerForActivityResult.
     *Lanzamiento de la actividad: Lanzas la actividad a través del lanzador con un intent u otro parámetro.
     *Recepción del resultado:El resultado de la actividad se devuelve en un callback proporcionado al registrador.
     *
     * */
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar el diseño usando View Binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Configurar el ActivityResultLauncher para manejar el resultado del inicio de sesión
        signInLauncher = registerForActivityResult(
                //Crea el contrato con esta actividad (login)  y maneja el resultado que se espera
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Comprobar si el resultado es exitoso
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //IdpResponse clase de firebase que sirve para encapsular el resultado anterior para poder operar con el
                        IdpResponse response = IdpResponse.fromResultIntent(result.getData());
                        if (response != null) {
                            //Si la respuesta que devuelve firebase es distinto de null se realiza la autenticacion del usuario, y la informacion
                            //del usuario es almacenado en una variable de tipo FirebaseUser
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            //Si la informacion del usuario es distinto de null...
                            if (user != null) {
                                // Mostrar un mensaje de bienvenida con el nombre del usuario
                                Toast.makeText(this, "¡Bienvenido, " + user.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                                // Redirigir a la actividad principal
                                startActivity(new Intent(this, MainActivity.class));
                                finish(); // Finalizar la actividad de inicio de sesión
                            }
                        }
                    } else {
                        // Manejar errores o si el usuario cancela el inicio de sesión
                        Toast.makeText(this, "Inicio de sesión cancelado o fallido", Toast.LENGTH_SHORT).show();
                        if (result.getData() != null) {
                            IdpResponse response = IdpResponse.fromResultIntent(result.getData());
                            if (response != null && response.getError() != null) {
                                // Registrar el error en el log
                                Log.e("Login", "Error: " + response.getError());
                            }
                        }
                    }
                }
        );

        // Configurar el clic en el botón para iniciar sesión con Google
        binding.iniciarGoogle.setOnClickListener(v -> signInWithGoogle());

        // Configurar el clic en el botón para redirigir a la pantalla de registro
        binding.registrarse.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, registro.class);
            startActivity(intent);
        });


        binding.iniciarSesion.setOnClickListener(v->signInWithEmailPassword());

    }

    private void signInWithGoogle() {
        // Configurar los proveedores de inicio de sesión (en este caso, Google)
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Crear el intent para iniciar la actividad de FirebaseUI
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers) // Asignar los proveedores configurados
                .build();

        // Lanzar la actividad de inicio de sesión con el ActivityResultLauncher
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

        // Iniciar sesión con correo electrónico y contraseña
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si el inicio de sesión es exitoso, redirigir al usuario
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "¡Bienvenido, " + user.getEmail() + "!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish(); // Finalizar la actividad de inicio de sesión
                        }
                    } else {
                        // Si el inicio de sesión falla, mostrar mensaje de error
                        Toast.makeText(this, "Error de inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Verificar si hay un usuario autenticado al iniciar la actividad
        if (mAuth.getCurrentUser() != null) {
            // Si el usuario está autenticado, redirigir a la actividad principal
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Finalizar la actividad de inicio de sesión
        }
    }
}
