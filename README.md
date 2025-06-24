# Product Catalog Android App

A modern Android application built with Jetpack Compose that showcases a product catalog with user profile management. The app demonstrates best practices in Android development including clean architecture, dependency injection, and local database storage.

## 🚀 Features

### Product Catalog
- **Product List**: Browse through a curated list of products with images, ratings, and prices
- **Product Details**: View detailed information including descriptions, ratings, reviews, and stock status
- **Category Organization**: Products organized by categories (Electronics, Wearables, Accessories, etc.)
- **Stock Management**: Real-time stock status display

### User Profile Management
- **Profile Information**: Manage personal details including name, email, address, and date of birth
- **Avatar Upload**: Take photos using device camera for profile pictures
- **Profile Editing**: Easy-to-use form for updating profile information
- **Data Persistence**: All profile data stored locally using Room database

### Navigation & UI
- **Smooth Animations**: Elegant slide transitions between screens
- **Material Design 3**: Modern UI following Google's latest design guidelines
- **Responsive Design**: Optimized for different screen sizes
- **Bottom Navigation**: Intuitive navigation between main sections

## 🏗️ Architecture

The app follows **Clean Architecture** principles with clear separation of concerns:

```
├── data/
│   ├── database/          # Room database entities, DAOs, and database setup
│   ├── mapper/           # Data transformation between layers
│   ├── model/            # Domain models
│   └── repository/       # Repository implementations
├── di/                   # Dependency injection modules (Koin)
├── presentation/
│   └── viewmodel/        # ViewModels for UI state management
├── ui/
│   ├── components/       # Reusable UI components
│   ├── screens/          # Screen composables
│   └── theme/           # App theming and styling
├── utils/               # Utility classes
└── navigation/          # Navigation setup
```

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - Design system

### Architecture Components
- **Room Database** - Local data persistence
- **ViewModel** - UI state management
- **Navigation Compose** - Screen navigation
- **Koin** - Dependency injection

### Additional Libraries
- **Coil** - Image loading and caching
- **Accompanist Permissions** - Runtime permission handling
- **Compose Material Dialogs** - Date picker dialogs
- **CameraX** - Camera functionality

## 📱 Screenshots

### Home Screen
- Product grid with images, ratings, and prices
- Category-based organization
- Smooth scrolling experience

### Product Details
- High-resolution product images
- Detailed descriptions and specifications
- Rating and review information
- Add to cart functionality (UI ready)

### Profile Management
- User information display and editing
- Camera integration for avatar photos
- Date picker for birth date selection

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24 or higher
- Kotlin 1.8+

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd product-catalog-app
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build and Run**
   - Wait for Gradle sync to complete
   - Click "Run" or press `Ctrl+R` (Windows/Linux) or `Cmd+R` (Mac)

### First Launch
The app will automatically:
- Initialize the Room database
- Populate sample product data
- Create a default user profile

## 🗄️ Database Schema

### Products Table
```sql
CREATE TABLE products (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    price REAL NOT NULL,
    imageUrl TEXT NOT NULL,
    category TEXT NOT NULL,
    rating REAL NOT NULL,
    reviewCount INTEGER NOT NULL,
    inStock INTEGER NOT NULL DEFAULT 1
);
```

### User Profile Table
```sql
CREATE TABLE user_profile (
    id INTEGER PRIMARY KEY DEFAULT 1,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    email TEXT NOT NULL,
    address TEXT NOT NULL,
    dateOfBirth TEXT NOT NULL,
    avatarPath TEXT
);
```

## 🔧 Configuration

### Permissions
The app requires the following permissions:
- `CAMERA` - For taking profile photos
- `READ_EXTERNAL_STORAGE` - For accessing saved images
- `WRITE_EXTERNAL_STORAGE` - For saving camera images (API < 29)

### File Provider
Camera functionality uses FileProvider for secure file sharing:
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

## 🎨 Customization

### Adding New Products
Products are initialized in `ProductRepositoryImpl.getSampleProducts()`. To add new products:

1. Add product data to the sample products list
2. Ensure images are accessible via URL
3. Assign appropriate categories

### Theming
Customize the app's appearance in:
- `ui/theme/Color.kt` - Color palette
- `ui/theme/Theme.kt` - Theme configuration
- `ui/theme/Type.kt` - Typography settings

### Navigation
Add new screens by:
1. Creating the screen composable in `ui/screens/`
2. Adding the route in `navigation/Navigation.kt`
3. Updating the navigation graph

## 🧪 Testing

### Unit Tests
Run unit tests with:
```bash
./gradlew test
```

### Instrumented Tests
Run instrumented tests with:
```bash
./gradlew connectedAndroidTest
```

## 📦 Build Variants

### Debug
- Includes debugging tools
- Detailed logging enabled
- Database inspection available

### Release
- Optimized for production
- ProGuard/R8 code shrinking
- Signed APK ready for distribution

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Maintain consistent formatting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Material Design** - For the beautiful design system
- **Jetpack Compose** - For the modern UI framework
- **Pexels** - For providing high-quality product images
- **Android Community** - For continuous inspiration and support

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check existing documentation
- Review the code comments for implementation details

---

**Built with ❤️ using Jetpack Compose and Modern Android Development practices**