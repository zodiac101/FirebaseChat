package com.example.rohil.firebasechat.dagger;

import com.example.rohil.firebasechat.BackgroundService;
import com.example.rohil.firebasechat.activity.GetDetailsActivity;
import com.example.rohil.firebasechat.viewModel.ChatViewModel;
import com.example.rohil.firebasechat.viewModel.FindUserViewModel;
import com.example.rohil.firebasechat.viewModel.GetDetailsViewModel;
import com.example.rohil.firebasechat.viewModel.MainViewModel;
import com.example.rohil.firebasechat.viewModel.SignUpViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Rohil on 6/9/2017.
 */

@Singleton
@Component(modules = {ChatModule.class})
public interface ChatComponent {

    void inject(ChatViewModel chatViewModel);

    void inject (MainViewModel mainViewModel);

    void inject (SignUpViewModel signUpViewModel);

    void inject (BackgroundService backgroundService);

    void inject (GetDetailsViewModel getDetailsViewModel);

    void inject (FindUserViewModel findUserViewModel);
}
