package app.gamenative.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val ADMIN_USER = "AndrewHayesGraves"
private const val ADMIN_PASS = "AndrewGraves25102006"

@Composable
fun AdminPanelScreen(
    onNavigateToMarketplace: () -> Unit,
    onDismiss: () -> Unit,
) {
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center,
    ) {
        if (!isLoggedIn) {
            AdminLoginCard(
                username = username,
                password = password,
                showPassword = showPassword,
                loginError = loginError,
                onUsernameChange = { username = it; loginError = false },
                onPasswordChange = { password = it; loginError = false },
                onTogglePassword = { showPassword = !showPassword },
                onLogin = {
                    if (username == ADMIN_USER && password == ADMIN_PASS) {
                        isLoggedIn = true
                        loginError = false
                    } else {
                        loginError = true
                    }
                },
                onDismiss = onDismiss,
            )
        } else {
            AdminDashboard(
                onNavigateToMarketplace = onNavigateToMarketplace,
                onLogout = { isLoggedIn = false; username = ""; password = "" },
                onDismiss = onDismiss,
            )
        }
    }
}

@Composable
private fun AdminLoginCard(
    username: String,
    password: String,
    showPassword: Boolean,
    loginError: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0000)),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFFB71C1C), Color(0xFF4A0000))),
                        RoundedCornerShape(14.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("Painel ADM", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Gamekt Pro Admin", color = Color(0xFFEF9A9A), fontSize = 13.sp)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Usuário", color = Color(0xFFEF9A9A)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Senha", color = Color(0xFFEF9A9A)) },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = Color(0xFFEF9A9A),
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (loginError) {
                Spacer(Modifier.height(6.dp))
                Text("Credenciais inválidas", color = Color(0xFFFF5252), fontSize = 13.sp)
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Entrar", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.height(10.dp))
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = Color(0xFFEF9A9A))
            }
        }
    }
}

@Composable
private fun AdminDashboard(
    onNavigateToMarketplace: () -> Unit,
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0000)),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Brush.radialGradient(listOf(Color(0xFFB71C1C), Color(0xFF4A0000))), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("ADM", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Painel ADM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Bem-vindo, AndrewHayesGraves", color = Color(0xFFEF9A9A), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(20.dp))
            Divider(color = Color(0xFF3A0000))
            Spacer(Modifier.height(16.dp))
            AdminActionCard(
                icon = Icons.Filled.ShoppingCart,
                title = "Loja Mercado Livre",
                subtitle = "Gerenciar estoque e acessórios",
                onClick = onNavigateToMarketplace,
            )
            Spacer(Modifier.height(12.dp))
            AdminStatRow("Jogos Instalados", "Gerenciar jogos no dispositivo")
            AdminStatRow("Downloads Ativos", "Ver fila de download")
            AdminStatRow("Usuários Conectados", "Ver sessões ativas")
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onLogout) {
                    Text("Sair da conta ADM", color = Color(0xFFEF9A9A))
                }
                TextButton(onClick = onDismiss) {
                    Text("Fechar", color = Color(0xFF888888))
                }
            }
        }
    }
}

@Composable
private fun AdminActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0000)),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFFEF9A9A), modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Color(0xFFEF9A9A), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AdminStatRow(label: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(label, color = Color.White, fontSize = 14.sp)
            Text(subtitle, color = Color(0xFF888888), fontSize = 11.sp)
        }
        Text("→", color = Color(0xFFEF9A9A))
    }
    Divider(color = Color(0xFF2A0000))
}
