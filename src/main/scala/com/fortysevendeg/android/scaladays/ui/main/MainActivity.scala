/*
 * Copyright (C) 2015 47 Degrees, LLC http://47deg.com hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortysevendeg.android.scaladays.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.{Handler, Bundle}
import android.support.v4.app.FragmentActivity
import android.support.v7.app.{AppCompatActivity, ActionBarDrawerToggle}
import android.view.{Menu, MenuItem, View}
import com.fortysevendeg.android.scaladays.R
import com.fortysevendeg.android.scaladays.ui.about.AboutFragment
import com.fortysevendeg.android.scaladays.ui.places.PlacesFragment
import com.fortysevendeg.android.scaladays.ui.qrcode.QrCodeFragment
import com.fortysevendeg.android.scaladays.ui.menu.MenuSection._
import com.fortysevendeg.android.scaladays.ui.menu._
import com.fortysevendeg.android.scaladays.ui.schedule.ScheduleFragment
import com.fortysevendeg.android.scaladays.ui.social.SocialFragment
import com.fortysevendeg.android.scaladays.ui.speakers.SpeakersFragment
import com.fortysevendeg.android.scaladays.ui.sponsors.SponsorsFragment
import com.fortysevendeg.android.scaladays.utils.AlarmUtils
import com.fortysevendeg.macroid.extras.DrawerLayoutTweaks._
import com.fortysevendeg.macroid.extras.FragmentExtras._
import com.fortysevendeg.macroid.extras.ToolbarTweaks._
import com.localytics.android.{LocalyticsActivityLifecycleCallbacks, Localytics}
import macroid.FullDsl._
import macroid._

class MainActivity
  extends AppCompatActivity
  with Contexts[FragmentActivity]
  with Layout
  with IdGeneration { self =>

  var actionBarDrawerToggle: Option[ActionBarDrawerToggle] = None

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    setContentView(layout)

    getApplication.registerActivityLifecycleCallbacks(
      new LocalyticsActivityLifecycleCallbacks(this)
    )

    Localytics.registerPush(getString(R.string.google_project_number))

    toolBar foreach setSupportActionBar

    getSupportActionBar.setDisplayHomeAsUpEnabled(true)
    getSupportActionBar.setHomeButtonEnabled(true)

    AlarmUtils.setReloadJsonService(this)

    drawerLayout foreach { drawerLayout =>
      val drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openMenu, R.string.closeMenu) {
        override def onDrawerClosed(drawerView: View): Unit = {
          super.onDrawerClosed(drawerView)
          invalidateOptionsMenu()
          findFragmentById[MenuFragment](Id.menuFragment) map (_.showMainMenu)
        }

        override def onDrawerOpened(drawerView: View): Unit = {
          super.onDrawerOpened(drawerView)
          invalidateOptionsMenu()
        }
      }
      actionBarDrawerToggle = Some(drawerToggle)
      drawerLayout.setDrawerListener(drawerToggle)
    }

    if (savedInstanceState == null) {
      runUi(
        replaceFragment(
          builder = f[MenuFragment],
          id = Id.menuFragment,
          tag = Some(Tag.menuFragment)))
    }
  }

  override def onNewIntent(intent: Intent): Unit = {
    super.onNewIntent(intent)
    setIntent(intent)
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Unit =
    super.onActivityResult(requestCode, resultCode, data)

  override def onCreateOptionsMenu(menu: Menu): Boolean = super.onCreateOptionsMenu(menu)

  override def onPostCreate(savedInstanceState: Bundle): Unit = {
    super.onPostCreate(savedInstanceState)
    actionBarDrawerToggle foreach (_.syncState)

  }

  override def onConfigurationChanged(newConfig: Configuration): Unit = {
    super.onConfigurationChanged(newConfig)
    actionBarDrawerToggle foreach (_.onConfigurationChanged(newConfig))
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (actionBarDrawerToggle.isDefined && actionBarDrawerToggle.get.onOptionsItemSelected(item)) true
    else super.onOptionsItemSelected(item)
  }

  def itemSelected(section: MenuSection.Value, title: String) {
    val builder = section match {
      case SPEAKERS => f[SpeakersFragment]
      case SCHEDULE => f[ScheduleFragment]
      case SOCIAL => f[SocialFragment]
      case CONTACTS => f[QrCodeFragment]
      case SPONSORS => f[SponsorsFragment]
      case PLACES => f[PlacesFragment]
      case ABOUT => f[AboutFragment]
      case _ => throw new IllegalStateException
    }
    runUi(
      (toolBar <~ tbTitle(title)) ~
        (drawerLayout <~ dlCloseDrawer(fragmentMenu)) ~
        replaceFragment(
          builder = builder,
          id = Id.mainFragment,
          tag = Some(Tag.mainFragment))
    )
  }

}
