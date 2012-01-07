package com.florianmski.tracktoid.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask;
import com.florianmski.tracktoid.trakt.tasks.post.PostTask.PostListener;
import com.jakewharton.trakt.entities.Response;

public class JoinFragment extends TraktFragment
{
	private EditText edtUsername;
	private EditText edtEmail;
	private EditText edtPassword;
	private Button btnGo;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
		super.onActivityCreated(savedInstanceState);

		btnGo.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final String username = edtUsername.getText().toString().trim();
				final String email = edtEmail.getText().toString().trim();
				final String password = Utils.SHA1(edtPassword.getText().toString().trim());

				if(username.equals(""))
					edtUsername.setError("Is your username a ninja?");
				else if(email.equals(""))
					edtEmail.setError("Is your email a ninja?");
				else if(password.equals(""))
					edtPassword.setError("Is your password a ninja?");
				else
				{
					tm.setAuthentication(username, password);

					new PostTask(tm, JoinFragment.this, tm.accountService().create(username, password, email), new PostListener() 
					{	
						@Override
						public void onComplete(Response r, boolean success) 
						{
							if(success)
							{
								SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
								prefs.edit()
								.putString("editTextUsername", username)
								.putString("editTextPassword", password)
								.putBoolean("sha1", true)
								.commit();
							}
							else
							{
								if(r != null && r.error != null)
								{
									if(r.error.toUpperCase().contains("USERNAME"))
										edtUsername.setError(r.error);
									else if(r.error.toUpperCase().contains("E-MAIL"))
										edtEmail.setError(r.error);
									else if(r.error.toUpperCase().contains("PASSWORD"))
										edtPassword.setError(r.error);
								}
							}
						}
					}).execute();
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_join, null);

		edtUsername = (EditText)v.findViewById(R.id.editTextUsername);
		edtEmail = (EditText)v.findViewById(R.id.editTextEmail);
		edtPassword = (EditText)v.findViewById(R.id.editTextPassword);
		btnGo = (Button)v.findViewById(R.id.buttonGo);

		return v;
	}
}