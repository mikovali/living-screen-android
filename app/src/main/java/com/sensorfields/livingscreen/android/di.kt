package com.sensorfields.livingscreen.android

import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sensorfields.livingscreen.android.domain.data.local.AlbumDao
import com.sensorfields.livingscreen.android.domain.data.local.ApplicationDb
import com.sensorfields.livingscreen.android.domain.data.remote.AlbumApi
import com.sensorfields.livingscreen.android.domain.data.remote.GetAlbumsResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun json(): Json = Json { ignoreUnknownKeys = true }

    @Singleton
    @Provides
    fun firebaseApp(@ApplicationContext context: Context): FirebaseApp {
        return FirebaseApp.initializeApp(context)!!
    }

    @Singleton
    @Provides
    fun firebaseAuth(firebaseApp: FirebaseApp): FirebaseAuth = FirebaseAuth(firebaseApp)
}

@Module
@InstallIn(ApplicationComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun applicationDb(@ApplicationContext context: Context): ApplicationDb {
        return Room.databaseBuilder(context, ApplicationDb::class.java, "application").build()
    }

    @Singleton
    @Provides
    fun albumDao(applicationDb: ApplicationDb): AlbumDao = applicationDb.albumDao()
}

@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun retrofit(json: Json): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://photoslibrary.googleapis.com/v1/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    fun albumApi(json: Json): AlbumApi {
        return object : AlbumApi {
            @OptIn(ImplicitReflectionSerializer::class)
            override suspend fun getAlbums(authorization: String): GetAlbumsResponse {
                delay(1000L)
                return json.fromJson(json.parseJson(getAlbumsResponse))
            }
        }
    }
}

private val getAlbumsResponse = """
{
  "albums": [
    {
      "id": "AKXdipv8C-y9OwkCw-YricCcGHzgDkSKoMiIqsbU_UHMbzmAriWrcgAwxoWedFA6aVllZuePV1NW",
      "title": "m-sport",
      "productUrl": "https://photos.google.com/lr/album/AKXdipv8C-y9OwkCw-YricCcGHzgDkSKoMiIqsbU_UHMbzmAriWrcgAwxoWedFA6aVllZuePV1NW",
      "mediaItemsCount": "409",
      "coverPhotoBaseUrl": "https://lh3.googleusercontent.com/lr/AFBm1_bDLrRkWXoTnzsLqweUG_tCIXRam-3NQovHITvtEwC_BevWW-0-RrxMAvRQ7iaTQd-aM-5zuiRjxriUamDTif9KP77DwAG2aOwjBvw9miMItYMMSk1aMLktvvFRDUr1_d_MDLErx4VPojIR77YYZHnyMUpkxaWejtg3JNXgkBPgcUzrQQCPy4CbnfIq8xPJ9SGmxUMahFCrBo30YcrEeJOGsXG8Ghts-arCg9Bt5p97Sc1dpxXwTnsm4zDsYG4gODijeJJ4a08r3JZV7bqgclBbqRsiuKXRq29BVFvSQp-iQ5vcK1RjTzZjkfsMZ5gr6kBIlSgxYAY-1uK4yiPXlcmRt1-H3aOfwPNMIGL49vZD5KySRnxT7mUsvmIaXkWwtzjDncS1xlGvr9WBnG4wlkp5GyuKH9fL2RrTUn0IqCprmE9Kvvyn9P0wW4MFr7xYCrRCO6ipC6QvXLuDFt3uxnGV3eFMqV0ZA5NYs5D0VFHAV8WEGcAUMwHtNosDdPJLROUOzgVF8NwIroPrgA2Xntp4wnrbz_XTRYpPFkaFBmy4CbDznVch4o-pJkpZH-GU-7IFTDLoEJCcE_kaWmIfS7VqKBHgElJ1LCH-T0BX4AcG9_g3MuisqMsv-lLEmJo9EmxebLCQ_86U80aGO3tqGQ3hhvRIJpowUT2h8NpGG2zzRVUIhE_2KnavYSSs549LXmJhdPjcc1fFf-epEo5yY9mSPVzsl0dDs3ExfuTNlHFCibGOHi_b2UUg_HAORQMb7NGBSDrO0OaIk1rLWQHQXjR0nlVS-iwCc0b4MGF1YlTUp0utx0XBvw8PMe0jB653AK6DKEIJ2A3AJAg0BbWi0N8FZmYiwjqp5sy0sVM",
      "coverPhotoMediaItemId": "AKXdipu8c9fsU1SXeMgeaPWHx0Q9KhJZNKOeg2fkTd4IRVnRqq26OhpBVpzZ06P8BNai-ptZ6xf_K5KZTTIXDvH5e-Fx_2P8zg"
    },
    {
      "id": "AKXdipvk3yZtcSFw95nfk_LWcrhGDHXfH6d_5HCKB_CEMxWTHjnDUBhLJADy-zyqkQw4WWqB-8tx",
      "title": "Väli Peldiku ehituse väljasõit Hiiumaale",
      "productUrl": "https://photos.google.com/lr/album/AKXdipvk3yZtcSFw95nfk_LWcrhGDHXfH6d_5HCKB_CEMxWTHjnDUBhLJADy-zyqkQw4WWqB-8tx",
      "mediaItemsCount": "81",
      "coverPhotoBaseUrl": "https://lh3.googleusercontent.com/lr/AFBm1_bF4xYOQ-Op98Z8s7uRmgy6QAH9vs4ppPMKKd2DipfK1ibQsWpL4yo3ppa5Pma1lx1lBPnIWINRqZOJRZRIrIsPbISIKU8ZnyKvnVBl6_cgSXgYccQFOwAsiTDQoUIOuxDNK-ywluVGSjdh48icg59D8xPe-1bUGUQwpp2p1danYsbFNNKGHH-5_l1y3U0tvXJJ34Ui45SNqh3A43yuduiUGmNu2oJaG429rPKpHiEe0qmix96ARt8CoqEpjD6EKmt7OqLZFQdejzBpaQmK8CahH2CAYX_vWlh9NF_x7kNXIoNZ2KHGSQE4TlgABuBXZNuWMfCBlPmA_5mTFfZWjO_n87o4a-0kOInACwSmUPdcqTpG-ReURKFOxaEJtoxaphHuYQO74bFW0tuVeKAtXR_r4Qi0WTa39X-3Wz_5PxrVwyMG-MZkPrULZFlQ3BS2Qtz1CP7j7sRkfSnYB-5ZwkCs_-gcHXAGrJ3SpcF2Px2WjkdLs6qUqSBvpiCygYrGSwLmAt8FnsPS9os_j7GLgQc8tl6TWiwU47VZCcKZcXn9PZ-ld86Kbju9gYIEyVLZf1nnEj2esSu4nDIerfHV1DR0tBooVI_WQPyAsZPKOMQWqhr9ECnMGOVvgm1_pwefrU1VTYYz_6b40hr0UtcmdR_0yYprYP6LPGoWM8WyKcx79z8e4KJhk1BAFiu4YgZYvxfjLj47NrBGs1Z9mF03k84pvcKer204MtYtSS9LMK5FqBKLqVQ-UfI9jv5mnn2p7qkcfzhMbDx8NGu6p6cgC2yWQahxzvzUP8qQ-WLpU1QGWy1bNyXk9KUdb_lNl3yc7jvtE1efKCBhlK9aKVe0WnsDylkC6OO0aiOBrZM",
      "coverPhotoMediaItemId": "AKXdiptIA-j8XydHox9FOxmiTGei4Z9Uvm0zrB5y0tGEzwzcYIJ2JR7tVO9wGmw4wt6rJUb-leLnVBUZ8nFtRe_kaGwlkNjO2A"
    },
    {
      "id": "AKXdips_SM7D66QLt9o4dAeAlm9pA5l-ewxaQRJvagsqDzzDi2Zj7UDca5N4mDuO0vAmhN3dfqBS",
      "title": "GT Qkkel",
      "productUrl": "https://photos.google.com/lr/album/AKXdips_SM7D66QLt9o4dAeAlm9pA5l-ewxaQRJvagsqDzzDi2Zj7UDca5N4mDuO0vAmhN3dfqBS",
      "mediaItemsCount": "387",
      "coverPhotoBaseUrl": "https://lh3.googleusercontent.com/lr/AFBm1_bRn_3KpxyTFVEBnzZ1FYPvzkkITneJWwWJ3QCzks9lZSJ5v0toDBSpde1C6sADhfZPyfj4IqsoBbM6HkorYPnoR0IittJhXhS6RlmluizNH58Q3wvETaPYaM7NudwURxn6vq3uPS7vC69tGx-AokDdCNAmB3Bv0Snc5znGQy5mRNgM3uoSHE4Gw5KvDPJ0qz9ntyPZQn0ehn9YhzdraDtl-aEHo658AD3NObmL7bMfjU7Jco8qKuIcX_JDYVm9yhSaNez4zodwpnzeNcNZq5k5Z7nrSk4Yb5BlVlS_5vT8GDysDoacPNVuFNqfg7odz3e3JWn36gCN2-ofQTnUFVyU1Q7b8P6pzLNIMIgsTKiR-QoWm3RcwKEy8YjXJtIU2Ba598Q1R_5ePPmnBC8UcfGXDtFz2GO3XDV-bUChfMHFkt-kmdZ4arw4uNaz_9ke3CbR42P4G3YGWj37M4QJ_jV1PVB0vH-p7m-RKuZJJOGPMapkv2Nf3E5tlHOaAeE-Sm5ZeSTzJWlHwItSHtK_eju5GqyFCbK5-kIUyQGoKUQ3NsVAyR6ArcP5ccemQBMUI3s0yA6_MLLuPPB_H7M3DCYGbVDvorI2bDkO5z5PZWQ2WFG9gFhyV3d9zJqvSwWvmC4j7mrsvSN-kkddshzRgjt2jgVKGTWcVlYS-TrOKW9JZLupkg4jc1xnrW3qQnFGRUK0asM9d6fFal3jxXq7zL6QL-eGgGLfi46CVw2f3HStyIG9OPBNY8SLedU1ZGIi-Q4wLYV9SEa6jCQIy61iRBLHGZ9W0jGKp6P8UO39AhhkoJbf4IrYsxSsGo595qi479qZVFrSYV76rFi9yvTeTuHIlTcixfRcQCC9qPI",
      "coverPhotoMediaItemId": "AKXdiptEsWaX54oB_SQ_WQABtjFMZRgzkiU8fGBF5LaZ7_s0b0FEHJqdy5lcUQY7P-b4LCHEvrJ4yUWeN3QjhXeuR_6SN492YQ"
    },
    {
      "id": "AKXdiptvyZcRKS5p42N_-plPwAfnojcBphBbEiSoVixP2UmnpTR5ZEWwIiC0_VB2P1haprnK51BX",
      "title": "Barca",
      "productUrl": "https://photos.google.com/lr/album/AKXdiptvyZcRKS5p42N_-plPwAfnojcBphBbEiSoVixP2UmnpTR5ZEWwIiC0_VB2P1haprnK51BX",
      "mediaItemsCount": "61",
      "coverPhotoBaseUrl": "https://lh3.googleusercontent.com/lr/AFBm1_Z2-vkVfPoiArj7b0MlkMUfXGMfgFSFzD0SjDPfv4RmOSutM2y1btUYmuKNuBrCvmvrYWIhmabH2q279eXprxYHWz2cNI41HdcJ1OZ93Rhq0pGwJDkpuPMD69XGXntK4UxNW1FWjlYPIefOA2JkNIgPueYQf-Lk696rF5RVDW1zznIYYY_1DkZDhlJBTdpLsOoEbjZfjq3UKtbEHjyHaSHPapFC-bZHpTPgfpW54p4-5S3YqYWNqIH40j5o6bvnPiECbh8KnbamKh0hRCo2sGjP1zf0a6e5n-T8RT-HzenS4G8Q86KIYMJvI3sM_BkwrUrTk3_rpkY7mpCy5VsHaX9L8R-7cL-WfvP7u320WdQCDrNCxBgg0naZ9gADh1cIpZjeAPSu_rQpu5R93DRoKEwutJurdTSTVNaH80eXAfYzIiG_O8iZUrUT0JuM7fa2cSD0iy3dS7wWk0rRLmoGNFECGlQBMQfIKN8Expwr37oOQl74H_sHHflNCLRmhkP2XBpLaAO7L_zAiVEmmZG_nWAj0hpdCnShtqUGeSZN3BOCtiy1-kgd4IdeCU97CRH-Vfwvg_1MUiS6oi_pKzIl6W3mDGUWRcxEwoR5vfHj4dOMW1xtHVMw-i5BnrKtCEtW7ng-m-6JdrXjMDFHASQFtjT11FFg8Mh9lsRJJWyS-57j7NBfelikgc-LuBYSvMpnQ5-aGFNHxwFurSTH9_JKiRnQBSckAZSd52RZ7vuVQ4IGiQqkgBLA9S_dFKnGUVtCsjLQtFnerVJy2RfOljPcWTPdcJ_QE4CcV-3YYdAWIDs0gW9PjXV38UjN_MDFB7rrfQzjwU98pUiu4wGSODG8vAhqvNJbQwOt6-b_OQY",
      "coverPhotoMediaItemId": "AKXdipv9Iq-wzx9Gb78_97CK4yBg18Fw7h5DYi56Xxd6yBdKGZQg2DuVtVwjSTy4WN17OyfO9DiMZs-9kgu2pqjhk4QtiWRNAA"
    },
    {
      "id": "AKXdipuVmjHigbEUU7n6XMLX7vRARvJiZCpL3pz9LHnvKMMSCAg7niQOFr9MJ3Iy-DkeqXZ55ws0",
      "title": "Lolita ja Juusu",
      "productUrl": "https://photos.google.com/lr/album/AKXdipuVmjHigbEUU7n6XMLX7vRARvJiZCpL3pz9LHnvKMMSCAg7niQOFr9MJ3Iy-DkeqXZ55ws0",
      "mediaItemsCount": "15",
      "coverPhotoBaseUrl": "https://lh3.googleusercontent.com/lr/AFBm1_Y6HaomizzDKFg3rXWAr4wHH7Xh7Gsp-Bz8pqQcFzC13Cuaiip3FG3_JOfB7PLUBkqiNbF0_JWu5U_ojfbk2oPO1DmKgLE11ddzVwbKGq7WRez1Pg4jcrU1jzZMC3Oha5oGaLuLdiwVaHyaHis_Iiyu1JAQngSpuyeFqijxmYxnqx5p6NyMvfnxL79yjiZPE15irxVpZ51nTL8b4U5KZwc9YtDdpun5S6d_675q-nnFxxGlIGgFG6Sg32-RWrNt1DnMN-f5IPUEc82R2UGp7yIRmosNN8bUZiShTCfX-ata1nK-TvNTcLOTeJ8NC49WwfQN5PRXXerKE6Qnq78OaLs124IyM1aiFu0VTHfADVlMWNpIdc1qj-2cXY9O6q0srg-hONW27Cx92A4HNKXUmA5NyrnO1KdYlkRyTFHUjsVoZuQf8EZYHrZdh-qS44J-WJIJP2PNP8xkRwwM868GiIN6gPjezMBTXLHAtw-uheXr_xB1UGa4AZsFL6VWjGT7QkOCYlI-ygHaWCqdcUiuY4d5TCm5H__T6BaZ9Qu8yDxI5TznVeDVXinuuaZAwUXNF_DjQNfZQw4nvWhZd6dTjRERlVoN8yssBLlVjQGH413Qmwstq-mj4KHz3EGqBQtV9dP7UGAcm_Ol1PpIx9Xz6HchzDXBm-_uKly4f5M8GSwYTg3KkS5MBBs8PvMaE2sXELNR2JHQG38JHisC2QoIbgyjibZeAjzyhoGXaethRVNOnXqYwNtt1Uby2r1AYrsEtaCknKyYBrxtTTgi_y7efn3E5EJmN7Jax1Ivr_RiAu7kP0H5Q9LLxVte41Y9m_Kn7JNZ-s7t_ZjtHpJwZiX_NVlCNxPHEHn7tYsGdA",
      "coverPhotoMediaItemId": "AKXdipsWdLedG1OUKh1ISAIKI9e6PLSX3sQL70rR738VCcNnpYlfjx8h2mXGZS7mpoHb7hynKzF4wbq_o6NUWaFZmRxlEeqkMw"
    },
    {
      "id": "AKXdiptK7lPrrdEGqwDlAVi1br6VpqV_aV5wuuu_9Z8Z3-a9l-PcsRJ41z7Pem28OEPZoH47Np6m",
      "title": "Saturday afternoon in Võru linn and Rõuge Parish",
      "productUrl": "https://photos.google.com/lr/album/AKXdiptK7lPrrdEGqwDlAVi1br6VpqV_aV5wuuu_9Z8Z3-a9l-PcsRJ41z7Pem28OEPZoH47Np6m",
      "mediaItemsCount": "17",
      "coverPhotoBaseUrl": "https://lh3.googleusercontent.com/lr/AFBm1_b7RrG-0b44nYkjKcIoOforfuHmYdvZC1PtNG2j_i1bPyYrWkUvXCKgdFoYHSlSQzjFpxRnpp_Ox0I3QWYgA6Xbog9AMhF-b6DWggMLThHlLGwf-r_zP698iaCEHGAIMp7Z3_4dXc3RhQm5WDk9nlKmfdKyKewbI_KuMZHqqBsnXjJccecr_cKUN9GMdKsiNiU9a3nFUld1RvzwuEYb5J3SF9SFCSju0fKJVaHMYZ2BrN6tYqCUipt6-lgB_1XshRk3N9Z1hTJCud984LHx_Mpo0XajdoJervuoHZBpmEXowvKVkIERoSLfqW3LlOUafATIp4ZRGBy1MzJOXRfTVrmUnioad_NHnN2W6YkMOs1cy2IDqDh5Zrk-izAfIQ75RQY8WH4kpFGQZu4RE6OsQNT0acdtF5xGyXTEZSj_MqjQb_i5R7LSbDx9VYUGrkJ2qVWf1xm_tMI5JI0-Lchjq5pIqH1vD0kJSXjtYRMK2YaOi6ektoxVNH3QzQh65fUErp8lJefozR_dc1Fumdnzir55zkgDWix-pqOnhmAVXWD8Uso-NxsasOLy_42SjzRBJ-ai_MMOsfcFCC1jY1fWeUjKiEbqobFo8y9E0bp4ZiTIQA3_wTZLnjftjZp1S7y-q5xsFfvlscYfl59wdUOFJsKVayR-MxpK4rdMRZXaZZBdR3xMnQI502mkmQl-QiAELF0lPAbbiNn5IZJ6IBtERiL3AEDmoc7-F_Rh9DrJXuiwSkxV6wThov3LOVf5orRBG8QC5dL6g0U20y-Jc7uPsZwR9dBf3VjGYuej1pahI5O7E7eBaJpVyYk6fmgOX5nKc5if9AId95CzTqbGFjQPUXWqX3_LKOnIwNvXXw",
      "coverPhotoMediaItemId": "AKXdiptUuKiC-Q7jD16GvRO6UjFatpm0gOxpRd6nHGPiqUIzG0Dv2pf8bd0yLi2Klss70KhIIGpf0VX4O0HkkDsiAhCFbs6hDQ"
    },
    {
      "id": "AKXdipt1klW6_l0_98uaMAXGSXiqJzi12Ps4-FncZTQBRiRzHi-Gkdddkf3eNmPYDE4IeBsqRHMn",
      "title": "fff",
      "productUrl": "https://photos.google.com/lr/album/AKXdipt1klW6_l0_98uaMAXGSXiqJzi12Ps4-FncZTQBRiRzHi-Gkdddkf3eNmPYDE4IeBsqRHMn",
      "isWriteable": true,
      "coverPhotoBaseUrl": "https://lh3.googleusercontent.com/lr/AFBm1_ZDJvZ0SYABz6PHNXvs8q_MfPvGq3HzFZ66XJoIvPvnankAYaB6pkrKNmATDoOJMXEKmSKfm6Fw5TJdChlcEHYZvLABk6dijLOMG9-ARkRYYtuhFS0aq0APbh4wZxsZQI0mB2G6drOikpvgJgmfxoNT4q54lWevipYjTcEPoL8TKvXus3gXCDZ8ySSYBiD3QufVhN1lRfRXeOveHDRXS115H7NY3Wv21Sas1-c9fmTX4-KbeTYrx5bsS98D_UJOaBEsX5yx9e8sfpGlLwQTjoeEy2Ou3oB7BGwaRcVMMYbY2WEbtCgNQC2xTNehNj8P72QdyjJsFcZYPBQVh-D9eRxXDrYMXclIu3oK8rwCsRirFJwXXcpteWiD-UDILgu72LNKcQ9cg_92nCtgSsu9ZHApzEydRi5g-tPXgOwKlfZ-T6AN6ErSq2UYTxoBCnGkfib7ZuwvG42N3tdBMqeOm2R8fzT1pH7wsE9fpLbWTK9nPlsQKUbOtziSFWoE9TLnlEtn69066raUGrehCVKTcol_klz9gGKsx-uwfr5yTnNXDfHJPQp30dOdZyOzt8iRWWrDDo5vKiypq0-dH3hqUTy6z9Haj6d5xymg6wd_qALGBw9WWZlvCmjSHYy-SFjjKyUhmyhi2J9fnaglppOt37BNvjGWcosilCWyPlF-HluEBgDTIoYS3mhpIw6WEhurp6S0bFqWKOcCtM9oY1FHgDjY-xz7LaXJcNXjB0T9uRVm2LoybT9UGhHGnwfXt0TuvF-Z5FpI_OvXbePy0mtsEFaoXeONIL9-oOj-YK2pSdan7rzc0S7xv8V4A4LPla0VtpB20RCdwOdWsTJTjoWjFl9LoDVGstQ8c6XD0rk"
    }
  ]
}
""".trimIndent()
