package com.example.stopthebomb.activities;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stopthebomb.R;
import com.example.stopthebomb.models.Comment;
import com.example.stopthebomb.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.List;
import java.util.stream.IntStream;

public class CommentsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private TextView commentCountTextView;
    private EditText commentEditText;
    private EditText authorEditText;
    private Button addCommentButton;
    private DatabaseHelper dbHelper;
    private List<Comment> commentList;

    // For editing
    private Comment commentBeingEdited = null;
    private Button cancelEditButton;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.comments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize UI elements
        commentCountTextView = findViewById(R.id.comment_count_text_view);
        commentEditText = findViewById(R.id.comment_edit_text);
        authorEditText = findViewById(R.id.author_edit_text);
        addCommentButton = findViewById(R.id.add_comment_button);
        cancelEditButton = findViewById(R.id.cancel_edit_button);

        // Initially hide the cancel button
        cancelEditButton.setVisibility(View.GONE);
        executorService = Executors.newSingleThreadExecutor();

        // Get database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // Load comments
        loadComments();

        // Set up click listeners
        addCommentButton.setOnClickListener(v -> {
            if (commentBeingEdited == null) {
                addComment();
            } else {
                updateComment();
            }
        });

        cancelEditButton.setOnClickListener(v -> cancelEdit());

        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(v -> finish());
    }

    private void loadComments() {
        // Load comments in the background
        executorService.execute(() -> {
            commentList = dbHelper.getAllComments();

            // Update the UI on the main thread
            runOnUiThread(() -> {
                // Set up adapter
                adapter = new CommentsAdapter(commentList);
                recyclerView.setAdapter(adapter);

                // Update comment count
                updateCommentCount();
            });
        });
    }

    private void updateCommentCount() {
        commentCountTextView.setText("Comments: " + commentList.size());
    }

    private void addComment() {
        String commentText = commentEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (author.isEmpty()) {
            author = "Anonymous";
        }

        String finalAuthor = author;
        executorService.execute(() -> {
            Comment comment = new Comment(commentText, finalAuthor);
            long id = dbHelper.addComment(comment);
            comment.id = (int) id;

            // Update the UI on the main thread
            runOnUiThread(() -> {
                // Clear input fields
                commentEditText.setText("");

                // Refresh the list
                commentList.add(0, comment);
                adapter.notifyItemInserted(0);
                updateCommentCount();
            });
        });

    }

    private void updateComment() {
        String commentText = commentEditText.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the comment
        executorService.execute(() -> {
            commentBeingEdited.text = commentText;
            boolean success = dbHelper.updateComment(commentBeingEdited);

            if (success) {
                // Find the position of the comment in the list
                int position = IntStream.range(0, commentList.size()).filter(i -> commentList.get(i).id == commentBeingEdited.id).findFirst().orElse(-1);

                // Update the UI on the main thread
                if (position != -1) {
                    runOnUiThread(() -> adapter.notifyItemChanged(position));
                }
            }

            // Reset edit mode
            runOnUiThread(this::cancelEdit);
        });
    }

    private void deleteComment(int commentId) {
        // Confirm deletion
        new AlertDialog.Builder(this)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    executorService.execute(() -> {
                        boolean success = dbHelper.deleteComment(commentId);

                        if (success) {
                            // Find and remove the comment from the list
                            int position = IntStream.range(0, commentList.size()).filter(i -> commentList.get(i).id == commentId).findFirst().orElse(-1);

                            // Update the UI on the main thread
                            if (position != -1) {
                                runOnUiThread(() -> {
                                    commentList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    updateCommentCount();
                                });
                            }
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void editComment(Comment comment) {
        // Set the comment being edited
        commentBeingEdited = comment;

        // Update UI
        commentEditText.setText(comment.text);
        authorEditText.setText(comment.author);
        authorEditText.setEnabled(false); // Don't allow changing the author

        addCommentButton.setText("Update Comment");
        cancelEditButton.setVisibility(View.VISIBLE);
    }

    private void cancelEdit() {
        // Reset editing state
        commentBeingEdited = null;

        // Update UI
        commentEditText.setText("");
        authorEditText.setEnabled(true);
        addCommentButton.setText("Add Comment");
        cancelEditButton.setVisibility(View.GONE);
    }



    // Adapter for the Comments RecyclerView
    private class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
        private List<Comment> commentList;

        public CommentsAdapter(List<Comment> commentList) {
            this.commentList = commentList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Comment comment = commentList.get(position);

            holder.authorTextView.setText(comment.author);
            holder.commentTextView.setText(comment.text);
            holder.dateTextView.setText("Posted: " + formatDate(comment.createdDate));

            if (comment.isEdited) {
                holder.editedTextView.setVisibility(View.VISIBLE);
                holder.editedTextView.setText("Edited: " + formatDate(comment.editedDate));
            } else {
                holder.editedTextView.setVisibility(View.GONE);
            }

            // Set up click listeners for edit and delete
            holder.editButton.setOnClickListener(v -> editComment(comment));
            holder.deleteButton.setOnClickListener(v -> deleteComment(comment.id));
        }

        private String formatDate(String dateString) {
            // Format date for display (could be enhanced to show relative time)
            return dateString;
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView authorTextView;
            TextView commentTextView;
            TextView dateTextView;
            TextView editedTextView;
            ImageButton editButton;
            ImageButton deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                authorTextView = itemView.findViewById(R.id.comment_author);
                commentTextView = itemView.findViewById(R.id.comment_text);
                dateTextView = itemView.findViewById(R.id.comment_date);
                editedTextView = itemView.findViewById(R.id.comment_edited);
                editButton = itemView.findViewById(R.id.edit_button);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
        }
    }
}
