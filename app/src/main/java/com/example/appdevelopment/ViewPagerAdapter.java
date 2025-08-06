import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdevelopment.OverviewFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment for the given position
        switch (position) {
            case 0:
                return new OverviewFragment(); // The Overview Fragment
            case 1:
                // return new TransactionsFragment(); // Example: Transactions Fragment
            case 2:
                // return new ReportFragment(); // Example: Reports Fragment
            default:
                return new OverviewFragment(); // Default fragment
        }
    }

    @Override
    public int getItemCount() {
        // The number of tabs/fragments you want to display
        return 3; // Example: Overview, Transactions, Reports
    }
}