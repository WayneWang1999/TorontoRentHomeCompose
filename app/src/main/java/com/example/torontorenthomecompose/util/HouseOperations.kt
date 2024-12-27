package com.example.torontorenthome.util


import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.example.torontorenthome.models.House
import com.google.firebase.firestore.FirebaseFirestore



import kotlin.random.Random

class HouseOperations(
    private val context: Context
) {
    private val db = FirebaseFirestore.getInstance()

    fun generateRandomHousesAndUpload() {
        val batch = db.batch() // Use batch writes for efficiency
        val housesCollection = db.collection("houses")

        val imageUrls = listOf(
            "https://images.unsplash.com/photo-1480074568708-e7b720bb3f09?q=80&w=2074&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1554995207-c18c203602cb?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1560185007-cde436f6a4d0?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://plus.unsplash.com/premium_photo-1675615667752-2ccda7042e7e?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://plus.unsplash.com/premium_photo-1661962331652-514803c02b8a?q=80&w=1931&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://media.istockphoto.com/id/479767332/photo/idyllic-home-with-covered-porch.jpg?s=1024x1024&w=is&k=20&c=HfFAagx5qICx6X7GiskbsKoAEzkWb9tAwmW19D0q9m8=",
            "https://media.istockphoto.com/id/598165834/photo/nice-curb-appeal-of-american-craftsman-style-house.jpg?s=1024x1024&w=is&k=20&c=8UjUowY71dCmYvfGHi4k5Jjk8mRRHTdjPic3xt74tdM=",
            "https://media.istockphoto.com/id/590059304/photo/wooden-walkout-deck-well-kept-garden-with-bushes-and-flowers.jpg?s=1024x1024&w=is&k=20&c=-hoF2xsxOMmlRhOpmlId_gEAfOw7yNbffzWoPb6J7bE=",
            "https://media.istockphoto.com/id/481653068/photo/large-back-yard-with-greenery-and-furnished-porch.jpg?s=1024x1024&w=is&k=20&c=CNvqwJgqfbFZFEv3eA_y0khTjSMlM09f4AKpE2uhpT4=",
            "https://media.istockphoto.com/id/576931188/photo/exterior-of-luxury-house-with-grass-filled-back-yard.jpg?s=1024x1024&w=is&k=20&c=L4BgnHptK0zEPDtEnb0J08iD9yzhI_xOYc5o5ZwtR6c=",
            "https://media.istockphoto.com/id/1440262289/photo/modern-custom-new-england-colonial-home-with-an-american-flag-on-a-sunny-day.jpg?s=2048x2048&w=is&k=20&c=MaCi2-dUxYfBp-8uLqnCuosUbaxmdTiD8S1fPRTlnrE=",
            "https://media.istockphoto.com/id/1344654402/photo/home-with-three-car-garage.jpg?s=2048x2048&w=is&k=20&c=j3Vmf0zT8wFFPIfw-7Jy0syz3NAa6pVnTaI2sr0KGG4=",
            "https://media.istockphoto.com/id/508882763/photo/luxury-house-exterior.jpg?s=2048x2048&w=is&k=20&c=eoYxc8Pn0UU83nqM7kC767oZKWBkN4Tsz-VORtqCiSQ=",
            "https://media.istockphoto.com/id/478398208/photo/modern-log-cabin-on-waterfront-with-rustic-feel.jpg?s=2048x2048&w=is&k=20&c=9qP1PiqqJfpghRzeLGJmdxmcWwgnZ6OS7b2c6vSMw2U=",
            "https://media.istockphoto.com/id/1146993778/photo/modern-suburban-house-exterior.jpg?s=1024x1024&w=is&k=20&c=EH_sPqbp58U_E7H8mGdNMMZqfwo3V0XfjSk2vRp5Cx8=",
            "https://images.unsplash.com/photo-1729606454985-684729455b18?q=80&w=1936&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1701202602671-34185a5ccf28?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://images.unsplash.com/photo-1701202778110-40a875280a92?q=80&w=2071&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            "https://media.istockphoto.com/id/155700839/photo/a-beautiful-home-available-for-rent.jpg?s=2048x2048&w=is&k=20&c=gakLVfBJAIPbYj_8EXKz8z9XKCUceY8KcrpDp_jGTRY=",
            "https://media.istockphoto.com/id/177709534/photo/sold-home-for-sale-real-estate-sign-and-house.jpg?s=2048x2048&w=is&k=20&c=nYoDxC9Ngati2MOaQcFVuJRX8fexMEB2q_DvIU14iAM=",
        )

        for (i in 1..50) {
            val latitude = Random.nextDouble(43.65, 43.78)
            val longitude = Random.nextDouble(-79.53, -79.33)
            // Generate random data
            val house = House(
                latitude = latitude,
                longitude = longitude,
                address = getStreetAddress(latitude, longitude),
                createTime = "${Random.nextInt(1, 100)} days",
                description = "This exquisite custom built home showcases unmatched beauty and exceptional craftsmanship, making it a true gem in the highly sought-after Bayview Woods neighborhood. Every detail reflects a custom design where no expense has been spared, featuring a thoughtfully laid-out floor plan that includes a formal living and dining area. The chef's kitchen, a culinary haven, was the birthplace of the renowned gourmet salad dressings created by \"\"Renee\"\" herself, offering an inspiring space for culinary creativity. Transitioning to the elegant family room, you'll find built-in shelving, a cozy gas fireplace, and multiple walkouts leading to a stunning garden oasis, perfect for hosting gatherings and entertaining guests. Ascend to the second floor via the convenient elevator, where the primary bedroom awaits, boasting a grand double door entry, its own fireplace, and two walkouts that lead to a private balcony, providing a serene retreat. This luxurious suite also features a personal dressing room with pocket doors, clothes carousel and an opulent six-piece ensuite bathroom, ensuring comfort and sophistication. The fully finished basement adds even more value to this remarkable home, complete with a recreation room, a wet bar for entertaining, and an additional bedroom, making it the perfect package for those seeking a lifestyle of elegance and convenience. This residence is ideally situated near an array of boutiques, exquisite dining options, golf courses, and country clubs, making it a perfect choice for those who appreciate a vibrant lifestyle. Additionally, it falls within a highly sought-after school district that includes an array of private schools, Steeleview PS, Zion Heights MS, and AY Jackson SS , all of which boast a rich legacy of academic achievement. Living here means not only enjoying luxurious amenities but also providing your family with access to top-tier education. **** EXTRAS **** Elevator, Garage Access From House, Generator, Two Central Air Conditioning Units, Heat Pump in Primary Bedroom, Built In Speakers, Heated Floors on Primary Ensuite and Main Bath, Heated Towel Rack in Main Bath, Cedar Closet (38056489)",
                bedrooms = Random.nextInt(1, 6),
                bathrooms = Random.nextInt(1,6),
                area = Random.nextInt(500,10000),
                price = Random.nextInt(1000000,100000000),
                imageUrl = listOf(imageUrls.random(),imageUrls.random(),imageUrls.random(),imageUrls.random(),imageUrls.random(),imageUrls.random(),imageUrls.random(),imageUrls.random(),imageUrls.random(),imageUrls.random()),

            )

            // Add to the batch
            val houseRef = housesCollection.document()
            batch.set(houseRef, house)
        }

        // Commit the batch
        batch.commit()
            .addOnSuccessListener {
                println("Successfully added  houses.")
            }
            .addOnFailureListener { e ->
                println("Error adding houses: ${e.message}")
            }
    }

    fun deleteAllHouses() {
        val housesCollection = db.collection("houses")

        housesCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }
                // Commit the batch
                batch.commit()
                    .addOnSuccessListener {
                        println("Successfully deleted all houses.")
                    }
                    .addOnFailureListener { e ->
                        println("Error deleting houses: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                println("Error fetching documents: ${e.message}")
            }
    }

    private fun getStreetAddress(latitude: Double, longitude: Double): String {
        // TODO: instantiate the geocoder class
        val geocoder = Geocoder(context)
        return try {
            // Retrieve location results using Geocoder
            val searchResults:MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            // Handle case where no results are found
            if (searchResults.isNullOrEmpty()) {
                return "No address found for the given location."
            }
            // Extract address details from the result
            val foundLocation: Address = searchResults[0]
            foundLocation.getAddressLine(0) ?: "Address not available"
        } catch (ex: Exception) {
            Log.e("TESTING", "Error while getting street address", ex)
            "Error while retrieving address: ${ex.localizedMessage}"
        }
    }
}