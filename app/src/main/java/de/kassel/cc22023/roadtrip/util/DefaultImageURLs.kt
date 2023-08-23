package de.kassel.cc22023.roadtrip.util

class DefaultImageURLs {
    companion object {
        fun getImageByName(normalName: String) : String? {
            val name= normalName.lowercase()
            if (name.contains("cash")) {
                return "https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxjYXNofGVufDB8fHx8MTY5MjYzNDE2Nnww&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("toiletries")) {
                return "https://images.unsplash.com/photo-1615518731106-5af6fe4ad188?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHx0b2lsZXRyaWVzfGVufDB8fHx8MTY5MjYzNDIzNHww&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("tent")) {
                return "https://images.unsplash.com/photo-1682685797769-481b48222adf?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MXwxfHNlYXJjaHwxfHx0ZW50fGVufDB8fHx8MTY5MjcwNDg2M3ww&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("sleeping bag")) {
                return "https://images.unsplash.com/photo-1486999619268-6aa409dbecd1?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxzbGVlcGluZyUyMGJhZ3xlbnwwfHx8fDE2OTI3MDQ5MTl8MA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("first aid")) {
                return "https://images.unsplash.com/photo-1600091474842-83bb9c05a723?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxmaXJzdCUyMGFpZCUyMGtpdHxlbnwwfHx8fDE2OTI3MDQ5NDh8MA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("map") || name.contains("navigation")) {
                return "https://images.unsplash.com/photo-1604357209793-fca5dca89f97?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxtYXB8ZW58MHx8fHwxNjkyNzA0OTk2fDA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("snacks")) {
                return "https://images.unsplash.com/photo-1621939514649-280e2ee25f60?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxzbmFja3N8ZW58MHx8fHwxNjkyNzA1MDMxfDA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("water")) {
                return "https://images.unsplash.com/photo-1602143407151-7111542de6e8?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHx3YXRlciUyMGJvdHRsZXxlbnwwfHx8fDE2OTI3MDUwNTR8MA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("backpack")) {
                return "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxiYWNrcGFja3xlbnwwfHx8fDE2OTI3MDUwNzN8MA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("weather") || name.contains("rain")) {
                return "https://images.unsplash.com/photo-1504616267454-5460d659c9be?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxyYWluY29hdHxlbnwwfHx8fDE2OTI3MDUxMjB8MA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("shoes")) {
                return "https://images.unsplash.com/photo-1487956382158-bb926046304a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHx3YWxraW5nJTIwc2hvZXN8ZW58MHx8fHwxNjkyNzA1MTU1fDA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("identification")) {
                return "https://images.unsplash.com/photo-1565688842882-e0b2693d349a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxpZGVudGlmaWNhdGlvbiUyMGNhcmR8ZW58MHx8fHwxNjkyNzA1MTgzfDA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("phone") || name.contains("charger")) {
                return "https://images.unsplash.com/photo-1557767382-97b28f5488e7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxwaG9uZSUyMGNoYXJnZXJ8ZW58MHx8fHwxNjkyNzA1MzE4fDA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("clothes")) {
                return "https://images.unsplash.com/photo-1540221652346-e5dd6b50f3e7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxjbG90aGVzfGVufDB8fHx8MTY5MjcwNTMzOHww&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("sunscreen")) {
                return "https://images.unsplash.com/photo-1594055103006-7871176f1a7e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxzdW5zY3JlZW58ZW58MHx8fHwxNjkyNzA1Mzc5fDA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("sunglasses")) {
                return "https://images.unsplash.com/photo-1511499767150-a48a237f0083?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxzdW5nbGFzc2VzfGVufDB8fHx8MTY5Mjc4MzQ1MXww&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("bike") || name.contains("bicycle")) {
                return "https://images.unsplash.com/photo-1485965120184-e220f721d03e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxiaWtlJTIwcmVwYWlyJTIwa2l0fGVufDB8fHx8MTY5MjcwNTQwNXww&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("helmet")) {
                return "https://images.unsplash.com/photo-1611004061856-ccc3cbe944b2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxoZWxtZXR8ZW58MHx8fHwxNjkyNzA1NDU0fDA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("drive") || name.contains("driving") || name.contains("car")) {
                return "https://images.unsplash.com/photo-1630406144797-821be1f35d75?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxkcml2ZXIlMjdzJTIwbGljZW5zZXxlbnwwfHx8fDE2OTI3MDU0Nzl8MA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("stove")) {
                return "https://images.unsplash.com/photo-1607324772107-8ad6740ca195?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxzdG92ZXxlbnwwfHx8fDE2OTI3MTY3NzN8MA&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("food")) {
                return "https://images.unsplash.com/photo-1504674900247-0877df9cc836?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxmb29kfGVufDB8fHx8MTY5MjcxNjgwMXww&ixlib=rb-4.0.3&q=80&w=200"
            }

            if (name.contains("cooking")) {
                return "https://images.unsplash.com/photo-1528712306091-ed0763094c98?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w0OTE5MTV8MHwxfHNlYXJjaHwxfHxjb29raW5nfGVufDB8fHx8MTY5MjcxNjgyNHww&ixlib=rb-4.0.3&q=80&w=200"
            }

            return null
        }
    }
}