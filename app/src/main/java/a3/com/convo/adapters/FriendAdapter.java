package a3.com.convo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import a3.com.convo.Constants;
import a3.com.convo.GlideApp;
import a3.com.convo.R;

/** The FriendAdapter handles the selection of the friend(s) you want to play with. These friends
 *  are both friends on Facebook and registered on the app. We will now create an adapter for FB
 *  friends you'd like to invite.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context context;
    // Your friends usernames
    private ArrayList<String> myFriends;
    // Selected position on RV/selected friend(s)
    private int selectedPos = RecyclerView.NO_POSITION;
    private String selectedFriend;
    // For display
    private ParseUser currentFriend;
    private String profPic;
    private String name;
    private RecyclerViewItemClickListener friendSelectedCheck;

    // Brings friends in to adjust into RV
    public FriendAdapter(ArrayList<String> friends, RecyclerViewItemClickListener friendSelectedCheck) {
        myFriends = friends;
        this.friendSelectedCheck = friendSelectedCheck;
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View friendView = inflater.inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(friendView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendAdapter.ViewHolder holder, @NonNull int position) {
        String friend = null;
        // Check if position on view holder is less than/equal to array size
        if (position <= myFriends.size()) {
            // Highlights clicked item view
            holder.itemView.setBackgroundColor(selectedPos == position ? Color.rgb(229, 229, 229) : Color.TRANSPARENT);
            // Get friend
            friend = myFriends.get(position);
        }
        // Find friend in background
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(friend, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (object != null) {
                    // Get object's values from parse
                    currentFriend = object;
                    profPic = currentFriend.getString(Constants.PROF_PIC_URL);
                    name = currentFriend.getString(Constants.NAME);

                    // Set friend's photo
                    if (profPic != null && context != null && holder.ivFriend != null) {
                        GlideApp.with(context)
                                .load(profPic)
                                .circleCrop()
                                .into(holder.ivFriend);
                    }

                    // Set friends name
                    holder.tvFriend.setText(name);
                } else if (e != null) {
                    Toast.makeText(context, "Objects may be empty.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivFriend;
        public TextView tvFriend;

        public ViewHolder (View friendView) {
            super(friendView);

            ivFriend = friendView.findViewById(R.id.friend_iv);
            tvFriend = friendView.findViewById(R.id.friend_tv);

            friendView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get friend position
            int position = getAdapterPosition();
            // Make sure the position is valid/actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // Get selected friend to play with
                selectedFriend = myFriends.get(position);
                System.out.println("/" + selectedFriend);
                friendSelectedCheck.onItemClick(view, position);
            }

            // Highlight selected friend(s)
            notifyItemChanged(selectedPos);
            selectedPos = getAdapterPosition();
            notifyItemChanged(selectedPos);
        }
    }

    // Passes selected friend onto game fragment
    public String getSelectedFriend() {
        return selectedFriend;
    }

}
