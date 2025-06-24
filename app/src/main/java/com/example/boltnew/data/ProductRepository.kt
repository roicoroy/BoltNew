package com.example.boltnew.data

object ProductRepository {
    fun getProducts(): List<Product> {
        return listOf(
            Product(
                id = 1,
                name = "Wireless Headphones",
                description = "Premium wireless headphones with active noise cancellation and 30-hour battery life. Experience crystal-clear audio quality with deep bass and crisp highs. Perfect for music lovers and professionals.",
                price = 199.99,
                imageUrl = "https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg",
                category = "Electronics",
                rating = 4.5f,
                reviewCount = 1250
            ),
            Product(
                id = 2,
                name = "Smart Watch",
                description = "Advanced fitness tracking smartwatch with heart rate monitoring, GPS, and 7-day battery life. Track your workouts, monitor your health, and stay connected with smart notifications.",
                price = 299.99,
                imageUrl = "https://images.pexels.com/photos/437037/pexels-photo-437037.jpeg",
                category = "Wearables",
                rating = 4.3f,
                reviewCount = 890
            ),
            Product(
                id = 3,
                name = "Laptop Backpack",
                description = "Durable and stylish laptop backpack with multiple compartments and USB charging port. Perfect for students and professionals who need to carry their tech gear safely and comfortably.",
                price = 79.99,
                imageUrl = "https://images.pexels.com/photos/2905238/pexels-photo-2905238.jpeg",
                category = "Accessories",
                rating = 4.7f,
                reviewCount = 2100
            ),
            Product(
                id = 4,
                name = "Bluetooth Speaker",
                description = "Portable waterproof Bluetooth speaker with 360-degree sound and 12-hour battery life. Take your music anywhere with this compact yet powerful speaker that delivers rich, immersive audio.",
                price = 89.99,
                imageUrl = "https://images.pexels.com/photos/1649771/pexels-photo-1649771.jpeg",
                category = "Audio",
                rating = 4.4f,
                reviewCount = 756
            ),
            Product(
                id = 5,
                name = "Gaming Mouse",
                description = "High-precision gaming mouse with customizable RGB lighting and programmable buttons. Designed for competitive gaming with ultra-responsive sensors and ergonomic design for extended gaming sessions.",
                price = 59.99,
                imageUrl = "https://images.pexels.com/photos/2115257/pexels-photo-2115257.jpeg",
                category = "Gaming",
                rating = 4.6f,
                reviewCount = 1890
            ),
            Product(
                id = 6,
                name = "Wireless Charger",
                description = "Fast wireless charging pad compatible with all Qi-enabled devices. Sleek design with LED indicator and overcharge protection. Simply place your device and enjoy hassle-free charging.",
                price = 34.99,
                imageUrl = "https://images.pexels.com/photos/4526414/pexels-photo-4526414.jpeg",
                category = "Accessories",
                rating = 4.2f,
                reviewCount = 543
            ),
            Product(
                id = 7,
                name = "4K Webcam",
                description = "Ultra HD 4K webcam with auto-focus and built-in microphone. Perfect for video conferencing, streaming, and content creation. Features advanced image processing for crystal-clear video quality.",
                price = 129.99,
                imageUrl = "https://images.pexels.com/photos/4219654/pexels-photo-4219654.jpeg",
                category = "Electronics",
                rating = 4.5f,
                reviewCount = 672
            ),
            Product(
                id = 8,
                name = "Phone Stand",
                description = "Adjustable aluminum phone stand with 360-degree rotation. Compatible with all smartphone sizes and tablets. Perfect for video calls, watching content, or hands-free use at your desk.",
                price = 24.99,
                imageUrl = "https://images.pexels.com/photos/1841841/pexels-photo-1841841.jpeg",
                category = "Accessories",
                rating = 4.8f,
                reviewCount = 1456
            )
        )
    }
    
    fun getProductById(id: Int): Product? {
        return getProducts().find { it.id == id }
    }
}