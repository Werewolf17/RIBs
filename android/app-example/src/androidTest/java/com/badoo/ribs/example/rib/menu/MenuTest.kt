package com.badoo.ribs.example.rib.menu

import android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.badoo.common.ribs.RibsRule
import com.badoo.ribs.RibTestActivity
import com.badoo.ribs.core.Rib
import com.badoo.ribs.example.rib.menu.Menu.Input.SelectMenuItem
import com.badoo.ribs.example.rib.menu.Menu.MenuItem.FooBar
import com.badoo.ribs.example.rib.menu.Menu.MenuItem.HelloWorld
import com.badoo.ribs.example.rib.menu.builder.MenuBuilder
import com.badoo.ribs.example.rib.menu.element.MenuElement
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import io.reactivex.observers.TestObserver
import org.junit.Rule
import org.junit.Test

class MenuTest {

    @get:Rule
    val ribsRule = RibsRule(this::buildRib)

    private val menu = MenuElement()

    private val menuInput = PublishRelay.create<Menu.Input>()
    private val menuOutput = PublishRelay.create<Menu.Output>()

    @Test
    fun initialState_noSelectedElements() {
        menu.assertNothingSelected()
    }

    @Test
    fun selectItemInput_selectsItem() {
        acceptInput(SelectMenuItem(HelloWorld))

        menu.helloItem.assertIsSelected()
    }

    @Test
    fun clickOnItem_doesNotSelectItem() {
        menu.dialogsItem.click()

        menu.dialogsItem.assertIsNotSelected()
    }

    @Test
    fun itemClick_producesSelectOutput() {
        val observer = menuOutput.subscribeOnTestObserver()

        menu.fooItem.click()

        observer.assertValue(Menu.Output.MenuItemSelected(FooBar))
    }

    @Test
    fun selectItemInputTwoTimes_displaysOnlyLastSelection() {
        acceptInput(SelectMenuItem(HelloWorld))
        acceptInput(SelectMenuItem(FooBar))

        menu.helloItem.assertIsNotSelected()
        menu.fooItem.assertIsSelected()
    }

    private fun acceptInput(input: Menu.Input) = runOnUiThread {
        menuInput.accept(input)
    }

    private fun buildRib(ribTestActivity: RibTestActivity) =
        MenuBuilder(object : Menu.Dependency, Rib.Dependency by ribTestActivity {
            override fun menuInput(): ObservableSource<Menu.Input> = menuInput
            override fun menuOutput(): Consumer<Menu.Output> = menuOutput
        }).build()

    private fun <T> Observable<T>.subscribeOnTestObserver() = TestObserver<T>().apply {
        subscribe(this)
    }
}