package com.example.notex.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotexFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun createViewEditSaveBackAndRotateUnsavedDraft() {
        val suffix = java.util.UUID.randomUUID().toString().take(8)
        val originalTitle = "Trip notes $suffix"
        val editedTitle = "Updated trip $suffix"
        fun awaitText(text: String) {
            compose.waitUntil(10_000) {
                compose.onAllNodes(androidx.compose.ui.test.hasText(text)).fetchSemanticsNodes().isNotEmpty()
            }
        }
        awaitText("Add note")
        compose.onNodeWithText("Add note").performClick()
        compose.onNodeWithText("Save").performScrollTo().performClick()
        compose.onNodeWithText("Enter a title to save this note.").assertIsDisplayed()
        compose.onNodeWithText("Title (required)").performTextInput(originalTitle)
        compose.onNodeWithText("Content (optional)").performTextInput("First line\nSecond line")
        compose.onNodeWithText("Save").performScrollTo().performClick()
        awaitText("Edit")
        awaitText(originalTitle)
        compose.onNodeWithText(originalTitle).assertIsDisplayed()
        compose.onNodeWithText("First line\nSecond line").assertIsDisplayed()
        compose.onNodeWithText("Edit").performClick()
        compose.onNodeWithText("Title (required)").performTextReplacement(editedTitle)
        compose.onNodeWithText("Save").performScrollTo().performClick()
        awaitText("Edit")
        awaitText(editedTitle)
        compose.onNodeWithText(editedTitle).assertIsDisplayed()
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Notes").assertIsDisplayed()
        compose.onNodeWithText(editedTitle).performClick()
        compose.onNodeWithText("Edit").performClick()
        compose.onNodeWithText("Content (optional)").performTextReplacement("Unsaved\nDraft")

        compose.activityRule.scenario.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        compose.waitUntil(10_000) {
            compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }
        compose.waitForIdle()
        compose.onNodeWithText("Content (optional)").assertTextContains("Unsaved\nDraft")
        compose.onNodeWithText("Title (required)").assertTextContains(editedTitle)
        // Also explicitly recreate the Activity to exercise its saved-state owner.
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Content (optional)").assertTextContains("Unsaved\nDraft")
        compose.onNodeWithText("Cancel").performScrollTo().performClick()
        compose.onNodeWithText("Discard changes?").assertIsDisplayed()
        compose.onNodeWithText("Keep editing").performClick()
        compose.onNodeWithText("Content (optional)").assertTextContains("Unsaved\nDraft")
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Discard changes").performClick()
        awaitText("Edit")
        awaitText(editedTitle)
        compose.onNodeWithText(editedTitle).assertIsDisplayed()
        compose.onNodeWithText("First line\nSecond line").assertIsDisplayed()
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Notes").assertIsDisplayed()
    }
}
