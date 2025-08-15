import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appdevelopment.OverviewFragment;

/**
 * Adapter cho ViewPager2 để quản lý các Fragment
 * Hiện tại chỉ hỗ trợ OverviewFragment
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor của ViewPagerAdapter
     * @param fragmentActivity FragmentActivity chứa ViewPager
     */
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Phương thức tạo Fragment cho mỗi vị trí
     * @param position Vị trí của Fragment
     * @return Fragment tương ứng với vị trí
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về Fragment tương ứng với vị trí
        switch (position) {
            case 0:
                return new OverviewFragment(); // Fragment tổng quan
            case 1:
                // return new TransactionsFragment(); // Fragment giao dịch (chưa implement)
            case 2:
                // return new ReportFragment(); // Fragment báo cáo (chưa implement)
            default:
                return new OverviewFragment(); // Fragment mặc định
        }
    }

    /**
     * Phương thức trả về tổng số Fragment
     * @return Số lượng Fragment (3)
     */
    @Override
    public int getItemCount() {
        // Số lượng tab/Fragment muốn hiển thị
        return 3; // Tổng quan, Giao dịch, Báo cáo
    }
}