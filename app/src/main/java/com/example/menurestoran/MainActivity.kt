package com.example.menurestoran

import java.util.Locale
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import org.json.JSONArray
import org.json.JSONObject

// --- Data Model ---
data class MenuItem(
    val id: Int,
    val name: String,
    val category: String,
    val price: String,
    val priceValue: Int,
    val description: String,
    val icon: ImageVector,
    val imageUris: List<String> = emptyList(),
    val stock: Int = 0 // Added stock field
)

data class PromoBanner(
    val title: String,
    val description: String
)

val menuList = mutableStateListOf(
    // Makanan
    MenuItem(1, "Nasi Goreng Spesial", "Makanan", "Rp 25.000", 25000, "Nasi goreng dengan telur mata sapi, ayam suwir, dan kerupuk udang.", Icons.Default.Restaurant, stock = 20),
    MenuItem(2, "Mie Ayam Bakso", "Makanan", "Rp 20.000", 20000, "Mie telur kenyal dengan topping ayam bumbu kecap and bakso sapi asli.", Icons.Default.Fastfood, stock = 15),
    MenuItem(3, "Sate Ayam Madura", "Makanan", "Rp 30.000", 30000, "10 tusuk sate ayam pilihan dengan bumbu kacang khas Madura.", Icons.Default.Restaurant, stock = 10),
    MenuItem(6, "Ayam Penyet", "Makanan", "Rp 22.000", 22000, "Ayam goreng yang dipenyet dengan sambal terasi pedas mantap.", Icons.Default.Restaurant, stock = 12),
    MenuItem(7, "Gado-Gado", "Makanan", "Rp 18.000", 18000, "Sayuran segar dengan bumbu kacang kental dan kerupuk.", Icons.Default.Restaurant, stock = 8),

    // Minuman
    MenuItem(4, "Es Teh Manis", "Minuman", "Rp 5.000", 5000, "Teh seduh segar dengan gula asli and es batu kristal.", Icons.Default.LocalDrink, stock = 50),
    MenuItem(5, "Jus Alpukat", "Minuman", "Rp 15.000", 15000, "Jus alpukat mentega segar dengan topping susu kental manis.", Icons.Default.Coffee, stock = 25),
    MenuItem(8, "Es Jeruk Peras", "Minuman", "Rp 10.000", 10000, "Jeruk peras murni yang segar and kaya vitamin C.", Icons.Default.LocalDrink, stock = 30),
    MenuItem(9, "Kopi Susu", "Minuman", "Rp 12.000", 12000, "Perpaduan kopi robusta dan susu kental manis yang pas.", Icons.Default.Coffee, stock = 40),
    MenuItem(10, "Es Campur", "Minuman", "Rp 15.000", 15000, "Aneka buah, jelly, dan sirup dalam serutan es segar.", Icons.Default.LocalDrink, stock = 20),

    // Camilan
    MenuItem(11, "Pisang Goreng Keju", "Camilan", "Rp 12.000", 12000, "Pisang goreng renyah dengan topping parutan keju melimpah.", Icons.Default.Fastfood, stock = 25),
    MenuItem(12, "Bakwan Sayur", "Camilan", "Rp 10.000", 10000, "Gorengan sayur renyah dengan sambal kacang atau cabe rawit.", Icons.Default.Fastfood, stock = 30),
    MenuItem(13, "Singkong Goreng", "Camilan", "Rp 10.000", 10000, "Singkong empuk yang digoreng garing berbumbu bawang putih.", Icons.Default.Fastfood, stock = 15),
    MenuItem(14, "Cireng Bumbu Rujak", "Camilan", "Rp 12.000", 12000, "Aci digoreng renyah dicocol dengan bumbu rujak pedas manis.", Icons.Default.Fastfood, stock = 20),
    MenuItem(15, "Tahu Isi", "Camilan", "Rp 10.000", 10000, "Tahu goreng dengan isian sayuran gurih dan renyah.", Icons.Default.Fastfood, stock = 18)
)

// --- SharedPreferences Helper ---
class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("RestoPrefs", Context.MODE_PRIVATE)

    fun saveData(name: String, address: String, desc: String, hours: String, isDark: Boolean, profileImageUri: String? = null, promoBanners: List<PromoBanner>? = null) {
        sharedPreferences.edit().apply {
            putString("resto_name", name)
            putString("resto_address", address)
            putString("resto_desc", desc)
            putString("resto_hours", hours)
            putBoolean("is_dark_mode", isDark)
            putString("profile_image_uri", profileImageUri)
            
            promoBanners?.let {
                val jsonArray = JSONArray()
                it.forEach { banner ->
                    val obj = JSONObject()
                    obj.put("title", banner.title)
                    obj.put("description", banner.description)
                    jsonArray.put(obj)
                }
                putString("promo_banners", jsonArray.toString())
            }
            apply()
        }
    }

    fun getData(): Map<String, Any?> {
        val bannersJson = sharedPreferences.getString("promo_banners", null)
        val banners = mutableListOf<PromoBanner>()
        if (bannersJson != null) {
            val jsonArray = JSONArray(bannersJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                banners.add(PromoBanner(obj.getString("title"), obj.getString("description")))
            }
        } else {
            // Default banners if none saved
            banners.add(PromoBanner("Diskon 50%", "Untuk pengguna baru dengan kode 'VIBEBARU'"))
            banners.add(PromoBanner("Beli 1 Gratis 1", "Setiap hari Jumat untuk semua minuman dingin"))
            banners.add(PromoBanner("Gratis Ongkir", "Minimal belanja Rp 50.000 untuk area sekitar"))
        }

        return mapOf(
            "name" to (sharedPreferences.getString("resto_name", "Restoran Vibe") ?: "Restoran Vibe"),
            "address" to (sharedPreferences.getString("resto_address", "Jl. Kuliner No. 123, Jakarta") ?: "Jl. Kuliner No. 123, Jakarta"),
            "desc" to (sharedPreferences.getString("resto_desc", "Menyediakan masakan nusantara dengan cita rasa otentik.") ?: "Menyediakan masakan nusantara dengan cita rasa otentik."),
            "hours" to (sharedPreferences.getString("resto_hours", "09:00 - 21:00") ?: "09:00 - 21:00"),
            "isDark" to sharedPreferences.getBoolean("is_dark_mode", false),
            "profileImageUri" to sharedPreferences.getString("profile_image_uri", null),
            "promoBanners" to banners
        )
    }
}

// --- Theme ---
val ToscaGreen = Color(0xFF008080)
val LightTosca = Color(0xFFE0F2F1)
val DarkTosca = Color(0xFF004D40)

// Specific colors for Dark Mode to avoid being too bright
val MutedTosca = Color(0xFF006666) 
val SurfaceDark = Color(0xFF1E1E1E)

@Composable
fun RestoTheme(isDark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = ToscaGreen, // Keep for brand consistency but use muted elsewhere if needed
            onPrimary = Color.White,
            secondary = MutedTosca,
            surface = SurfaceDark,
            background = Color(0xFF121212),
            onSurface = Color(0xFFE0E0E0),
            onBackground = Color.White
        )
    } else {
        lightColorScheme(
            primary = ToscaGreen,
            onPrimary = Color.White,
            secondary = DarkTosca,
            surface = Color.White,
            background = Color(0xFFF0F7F7)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

// --- Main Activity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val prefManager = remember { PreferenceManager(context) }
            val initialData = prefManager.getData()
            var isDark by remember { mutableStateOf(initialData["isDark"] as Boolean) }

            RestoTheme(isDark = isDark) {
                MainNavigation(isDark, onThemeToggle = {
                    isDark = it
                    val d = prefManager.getData()
                    prefManager.saveData(
                        d["name"] as String,
                        d["address"] as String,
                        d["desc"] as String,
                        d["hours"] as String,
                        it,
                        d["profileImageUri"] as String?,
                        d["promoBanners"] as List<PromoBanner>?
                    )
                })
            }
        }
    }
}

@Composable
fun MainNavigation(isDark: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val navController = rememberNavController()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "splash") {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.RestaurantMenu, null) },
                        label = { Text("Menu") },
                        selected = currentRoute?.startsWith("menu") == true,
                        onClick = { navController.navigate("menu/Semua") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, null) },
                        label = { Text("Profil") },
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController, 
            startDestination = "splash",
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn() + slideInHorizontally { it } },
            exitTransition = { fadeOut() + slideOutHorizontally { -it } },
            popEnterTransition = { fadeIn() + slideInHorizontally { -it } },
            popExitTransition = { fadeOut() + slideOutHorizontally { it } }
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable(
                "menu/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType; defaultValue = "Semua" })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Semua"
                MenuScreen(navController, category)
            }
            composable(
                "detail_menu/{menuId}", 
                arguments = listOf(navArgument("menuId") { type = NavType.IntType })
            ) { backStackEntry ->
                val menuId = backStackEntry.arguments?.getInt("menuId") ?: 0
                DetailMenuScreen(navController, menuId)
            }
            composable(
                "edit_menu/{menuId}",
                arguments = listOf(navArgument("menuId") { type = NavType.IntType })
            ) { backStackEntry ->
                val menuId = backStackEntry.arguments?.getInt("menuId") ?: 0
                EditMenuScreen(navController, menuId)
            }
            composable("profile") { ProfileScreen(navController, isDark, onThemeToggle) }
            composable("edit_profile") { EditProfileScreen(navController) }
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    val data = prefManager.getData()
    val name = data["name"] as String
    val profileImageUri = data["profileImageUri"] as String?

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(LightTosca),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = ToscaGreen
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ToscaGreen)
        }
    }
}

// --- Screens ---

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    val data = prefManager.getData()
    val profileImageUri = data["profileImageUri"] as String?
    val promoBanners = data["promoBanners"] as List<PromoBanner>
    
    val pagerState = rememberPagerState(pageCount = { promoBanners.size })

    val categories = listOf(
        Triple("Makanan", Icons.Default.Restaurant, "Utama"),
        Triple("Minuman", Icons.Default.LocalDrink, "Segar"),
        Triple("Camilan", Icons.Default.Fastfood, "Ringan")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(LightTosca),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(16.dp))
                }
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Selamat Datang di",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = data["name"] as String,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start
                )
            }
            }
        }

        item {
            // Promo Carousel Banner
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                Text("Promo Spesial", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ToscaGreen)
                ) { page ->
                    Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                        Column(modifier = Modifier.align(Alignment.CenterStart)) {
                            Text(
                                text = promoBanners[page].title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = promoBanners[page].description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterEnd)
                                .alpha(0.2f),
                            tint = Color.White
                        )
                    }
                }
                
                // Pager Indicator
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(promoBanners.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) ToscaGreen else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
            }
        }

        // Section Kategori dengan Icon
        item {
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                Text("Kategori", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    categories.forEach { (name, icon, sub) ->
                        CategoryCard(name, icon, sub) {
                            navController.navigate("menu/$name")
                        }
                    }
                }
            }
        }

        // Section Pilihan (Featured)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pilihan Terpopuler", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                TextButton(onClick = { navController.navigate("menu/Semua") }) {
                    Text("Lihat Semua", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        items(menuList.take(3)) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("detail_menu/${item.id}") },
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(LightTosca),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.imageUris.isNotEmpty()) {
                            AsyncImage(
                                model = item.imageUris[0],
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(item.icon, contentDescription = null, tint = ToscaGreen)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(item.name, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.price, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("| Stok: ${item.stock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.CategoryCard(name: String, icon: ImageVector, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary, 
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                name, 
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                subtitle, 
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f), 
                fontSize = 10.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavHostController, category: String = "Semua") {
    val filteredList = if (category == "Semua") {
        menuList
    } else {
        menuList.filter { it.category == category }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu: $category") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ToscaGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("detail_menu/${item.id}") },
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(LightTosca),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.imageUris.isNotEmpty()) {
                                AsyncImage(
                                    model = item.imageUris[0],
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(item.icon, contentDescription = null, tint = ToscaGreen)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.price, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.width(8.dp))
                                Text("Stok: ${item.stock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailMenuScreen(navController: NavHostController, menuId: Int) {
    val item = menuList.find { it.id == menuId } ?: return
    
    var rating by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { if (item.imageUris.isEmpty()) 1 else item.imageUris.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Menu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(LightTosca),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUris.isNotEmpty()) {
                    HorizontalPager(state = pagerState) { page ->
                        AsyncImage(
                            model = item.imageUris[page],
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Icon(item.icon, contentDescription = null, modifier = Modifier.size(100.dp), tint = ToscaGreen)
                }
            }
            
            if (item.imageUris.size > 1) {
                Row(
                    Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(item.imageUris.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) ToscaGreen else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(item.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.price, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        color = if (item.stock > 0) MaterialTheme.colorScheme.primaryContainer else Color.Red.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Stok: ${item.stock}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (item.stock > 0) MaterialTheme.colorScheme.onPrimaryContainer else Color.Red
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Rating
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < rating) Color(0xFFFFB300) else Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = index + 1 }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = item.description,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { navController.navigate("edit_menu/${item.id}") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Menu")
                    }
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kembali")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMenuScreen(navController: NavHostController, menuId: Int) {
    val item = menuList.find { it.id == menuId } ?: return
    
    var name by remember { mutableStateOf(item.name) }
    var category by remember { mutableStateOf(item.category) }
    var priceValue by remember { mutableStateOf(item.priceValue.toString()) }
    var stock by remember { mutableStateOf(item.stock.toString()) }
    var description by remember { mutableStateOf(item.description) }
    val imageUris = remember { mutableStateListOf(*item.imageUris.toTypedArray()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris.addAll(uris.map { it.toString() })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Menu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Menu") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = priceValue, onValueChange = { priceValue = it }, label = { Text("Harga (Angka)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stok") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            
            Text("Gambar Menu", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                imageUris.forEach { uri ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { imageUris.remove(uri) },
                            modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.5f), CircleShape).size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightTosca)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddAPhoto, null, tint = ToscaGreen)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }
                Button(
                    onClick = {
                        val index = menuList.indexOf(item)
                        if (index != -1) {
                            val priceInt = priceValue.toIntOrNull() ?: item.priceValue
                            val stockInt = stock.toIntOrNull() ?: item.stock
                            menuList[index] = item.copy(
                                name = name,
                                category = category,
                                priceValue = priceInt,
                                stock = stockInt,
                                price = "Rp " + String.format(Locale("in", "ID"), "%,d", priceInt).replace(',', '.'),
                                description = description,
                                imageUris = imageUris.toList()
                            )
                        }
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController, isDark: Boolean, onThemeToggle: (Boolean) -> Unit) {
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    val data = prefManager.getData()
    val profileImageUri = data["profileImageUri"] as String?

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Restoran") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ToscaGreen)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(data["name"] as String, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ProfileItem(Icons.Default.Place, "Alamat", data["address"] as String)
            ProfileItem(Icons.Default.Info, "Deskripsi", data["desc"] as String)
            ProfileItem(Icons.Default.Schedule, "Jam Buka", data["hours"] as String)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Theme Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, null, tint = ToscaGreen)
                    Spacer(Modifier.width(16.dp))
                    Text("Tema Gelap")
                }
                Switch(checked = isDark, onCheckedChange = onThemeToggle)
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { navController.navigate("edit_profile") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Edit Profil")
            }
        }
    }
}

@Composable
fun ProfileItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    val data = prefManager.getData()
    
    var name by remember { mutableStateOf(data["name"] as String) }
    var address by remember { mutableStateOf(data["address"] as String) }
    var desc by remember { mutableStateOf(data["desc"] as String) }
    var hours by remember { mutableStateOf(data["hours"] as String) }
    var profileImageUri by remember { mutableStateOf(data["profileImageUri"] as String?) }
    val isDark = data["isDark"] as Boolean
    
    val promoBanners = remember { mutableStateListOf(*(data["promoBanners"] as List<PromoBanner>).toTypedArray()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri?.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Profil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(LightTosca)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.AddAPhoto, null, tint = ToscaGreen)
                }
            }

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Restoran") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Jam Buka") }, modifier = Modifier.fillMaxWidth())
            
            HorizontalDivider()
            Text("Kelola Banner Promo", fontWeight = FontWeight.Bold)
            
            promoBanners.forEachIndexed { index, banner ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LightTosca.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Promo #${index + 1}", fontWeight = FontWeight.Bold, color = ToscaGreen)
                            IconButton(onClick = { promoBanners.removeAt(index) }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }
                        OutlinedTextField(
                            value = banner.title,
                            onValueChange = { promoBanners[index] = banner.copy(title = it) },
                            label = { Text("Judul Promo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = banner.description,
                            onValueChange = { promoBanners[index] = banner.copy(description = it) },
                            label = { Text("Keterangan") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            if (promoBanners.size < 5) {
                TextButton(
                    onClick = { promoBanners.add(PromoBanner("Promo Baru", "Keterangan promo...")) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tambah Promo")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }
                Button(
                    onClick = {
                        prefManager.saveData(name, address, desc, hours, isDark, profileImageUri, promoBanners.toList())
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}
