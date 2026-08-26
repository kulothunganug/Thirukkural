package com.kulothunganug.thirukkural.models

import kotlin.random.Random

/**
 * Thirukkural has a fixed, well-known number of couplets (kurals), numbered 1..1330 inclusive.
 * This is the single source of truth for that range so callers (widget, home screen, deep links)
 * never drift apart on hardcoded literals, and can validate untrusted ids (e.g. from deep links)
 * before querying the database.
 */
const val MIN_KURAL_ID = 1
const val MAX_KURAL_ID = 1330

fun randomKuralId() = Random.nextInt(MIN_KURAL_ID, MAX_KURAL_ID + 1)

fun isValidKuralId(id: Int) = id in MIN_KURAL_ID..MAX_KURAL_ID
