package com.uservoice.uservoicesdk.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.Session;
import com.uservoice.uservoicesdk.flow.SigninManager;
import com.uservoice.uservoicesdk.model.Comment;
import com.uservoice.uservoicesdk.model.Suggestion;
import com.uservoice.uservoicesdk.ui.DefaultCallback;

@SuppressLint("ValidFragment")
public class CommentDialogFragment extends DialogFragment {

	private final Suggestion suggestion;
	private final SuggestionDialogFragment suggestionDialog;

	public CommentDialogFragment(Suggestion suggestion, SuggestionDialogFragment suggestionDialog) {
		this.suggestion = suggestion;
		this.suggestionDialog = suggestionDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.post_a_comment);

		View view = getActivity().getLayoutInflater().inflate(R.layout.comment_dialog, null);
		final EditText textField = (EditText) view.findViewById(R.id.comment_edit_text);

		View email = view.findViewById(R.id.email);
		View name = view.findViewById(R.id.name);
		final EditText emailField = (EditText) email.findViewById(R.id.text_field);
		final EditText nameField = (EditText) name.findViewById(R.id.text_field);
		if (Session.getInstance().getUser() != null) {
			email.setVisibility(View.GONE);
			name.setVisibility(View.GONE);
		} else {
			emailField.setText(Session.getInstance().getEmail());
			((TextView) email.findViewById(R.id.header_text)).setText(R.string.your_email_address);
			nameField.setText(Session.getInstance().getName());
			((TextView) name.findViewById(R.id.header_text)).setText(R.string.your_name);
		}

		builder.setView(view);

		builder.setNegativeButton(R.string.cancel, null);

		final Activity context = getActivity();
		builder.setPositiveButton(R.string.post_comment, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String text = textField.getText().toString();
				if (!text.trim().isEmpty()) {
					SigninManager.signIn(getActivity(), emailField.getText().toString(), nameField.getText().toString(), new Runnable() {
						@Override
						public void run() {
							Comment.createComment(suggestion, text, new DefaultCallback<Comment>(getActivity()) {
								@Override
								public void onModel(Comment model) {
									Toast.makeText(context, R.string.msg_comment_posted, Toast.LENGTH_SHORT).show();
									suggestionDialog.commentPosted(model);
								}
							});
						}
					});
				}
			}
		});
		return builder.create();
	}
}
