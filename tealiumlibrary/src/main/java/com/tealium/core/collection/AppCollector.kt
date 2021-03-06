package com.tealium.core.collection

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.pm.PackageInfo
import android.os.Process
import com.tealium.core.*

interface AppData : Collector {
    val appRdns: String
    val appName: String
    val appBuild: String
    val appVersion: String
    val appMemoryUsage: Long
}

class AppCollector(private val context: Context) : Collector, AppData {

    override val name: String
        get() = "APP_COLLECTOR"
    override var enabled: Boolean = true

    private val activityManager = context.applicationContext.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager

    override val appRdns: String = context.applicationContext.packageName
    override val appName: String = if (context.applicationInfo.labelRes != 0) context.getString(context.applicationInfo.labelRes) else ""
    override val appBuild: String = getPackageContext().versionName?.toString() ?: ""
    override val appVersion: String = getPackageContext().versionCode.toString()
    override val appMemoryUsage: Long
        get() {
            var memoryUsage = 0L
            try {
                val pids = arrayOf( Process.myPid() )
                activityManager.getProcessMemoryInfo(pids.toIntArray()).forEach {
                    memoryUsage += it.totalPss
                }
                memoryUsage = memoryUsage.div(1024)
            } catch (e: Exception) {

            }
            return memoryUsage
        }

    private fun getPackageContext() : PackageInfo {
        return context.packageManager.getPackageInfo(context.packageName, 0)
    }

    override suspend fun collect(): Map<String, Any> {
        return mapOf(AppCollectorConstants.APP_RDNS to appRdns,
                    AppCollectorConstants.APP_NAME to appName,
                    AppCollectorConstants.APP_VERSION to appVersion,
                    AppCollectorConstants.APP_BUILD to appBuild,
                    AppCollectorConstants.APP_MEMORY_USAGE to appMemoryUsage)
    }

    companion object: CollectorFactory {

        override fun create(context: TealiumContext): Collector {
            return AppCollector(context.config.application)
        }
    }
}

val Collectors.App : CollectorFactory
    get() = AppCollector
