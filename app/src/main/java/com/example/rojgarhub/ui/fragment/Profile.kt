package com.example.a35b_crud.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rojgarhub.databinding.FragmentProfileBinding
import com.example.rojgarhub.repository.UserRepositoryImpl
import com.example.rojgarhub.viewModel.UserViewModel


class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding

    lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,
            container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var repo =UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        var currentUser = userViewModel.getCurrentUser()

        currentUser?.let {
            userViewModel.getUserFromDatabase(it.uid.toString()) { userModel, success, message ->
                if (success as Boolean) {
                    // Update UI with the user data
                    // This is now redundant with your observer, but I'm showing the proper call pattern
                } else {
                    // Handle error
                    // Maybe show a toast with the error message
                }
            }
        }

        userViewModel.userData.observe(requireActivity()){users->
            binding.profileEmail.text = users?.email
            binding.profileName.text = users?.firstName + " " +users?.lastName
        }

    }

}