package com.badoo.ribs.android.requestcode

import com.badoo.ribs.android.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import io.reactivex.Observable

interface RequestCodeBasedEventStream<T : RequestCodeBasedEvent> {

    fun events(client: RequestCodeClient): Observable<T>

    interface RequestCodeBasedEvent {
        val requestCode: Int
    }
}
