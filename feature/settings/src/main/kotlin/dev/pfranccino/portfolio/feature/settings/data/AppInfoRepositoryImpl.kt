package dev.pfranccino.portfolio.feature.settings.data

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.pfranccino.portfolio.feature.settings.domain.AppInfoRepository
import javax.inject.Inject

internal class AppInfoRepositoryImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : AppInfoRepository {

  override fun getVersionName(): String {
    val packageName = context.packageName
    val packageManager = context.packageManager
    val info: PackageInfo? = try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
      } else {
        packageManager.getPackageInfo(packageName, 0)
      }
    } catch (e: PackageManager.NameNotFoundException) {
      // La app siempre se encuentra a sí misma; si no, la versión queda vacía.
      null
    }
    return info?.versionName.orEmpty()
  }
}
