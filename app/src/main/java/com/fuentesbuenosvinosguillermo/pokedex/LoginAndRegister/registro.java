package com.fuentesbuenosvinosguillermo.pokedex.LoginAndRegister;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.ActivityRegistroBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
/**
 * Clase encargada de realizar el registro
 * */
public class registro extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Inicializa FirebaseAuth para manejar la autenticación de usuarios
        mAuth = FirebaseAuth.getInstance();
        // Botón para registrar al usuario (vinculado con el botón en el layout)
        Button registro = binding.registro;
        // Obtención de los valores de los campos de texto (EditText) para el correo y la contraseña
        final EditText emailEditText = binding.emailRegistro;
        final EditText passwordEditText = binding.introducePass;

        // Configuración del escuchador de clic para el botón de registro
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtención de los valores introducidos por el usuario (correo y contraseña)
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Verificar si los campos están vacíos, en cuyo caso mostramos un mensaje de error
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(registro.this, "Por favor, ingresa todos los campos.", Toast.LENGTH_SHORT).show();
                    return;  // Si los campos están vacíos, no continuar con el registro
                }
                // Intentar crear un usuario con correo y contraseña usando Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        // Agrega un comportamiento adicional al botón de registro, usando el listener de la tarea
                        .addOnCompleteListener(registro.this, task -> {
                            if (task.isSuccessful()) {
                                // Si el registro es exitoso, obtenemos al usuario creado
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Mostramos un mensaje de bienvenida con el correo del usuario
                                    Toast.makeText(registro.this, "Registro exitoso. Bienvenido, " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                    // Redirigimos al usuario a la pantalla principal de la aplicación
                                    Intent intent = new Intent(registro.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();  // Terminamos la actividad de registro para que no se pueda volver atrás
                                }
                            } else {
                                // Si ocurre un error en el registro, mostramos el mensaje de error correspondiente
                                Toast.makeText(registro.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
