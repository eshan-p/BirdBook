import ProfileIcon from "../common/ProfileIcon"
import { User } from "../../types/User"

function FriendCard({ user }: { user: User }) {
    return (
        <div className="flex items-center gap-4 px-2">
            {/* Profile Pic */}
            <ProfileIcon size="sm" />

            <div className="flex flex-col justify-center">
                <h3 className="text-sm font-semibold text-gray-900 leading-tight">
                    {user.username}
                </h3>

                <p className="text-xs text-gray-500">
                    {user.friends?.length || 0} friends
                </p>
            </div>
        </div>
    )
}

export default FriendCard
