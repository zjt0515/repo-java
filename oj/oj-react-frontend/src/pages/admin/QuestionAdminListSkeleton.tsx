import { Skeleton } from "@/components/ui/skeleton"
import { TableCell, TableRow } from "@/components/ui/table"

function QuestionAdminListSkeleton() {
  return Array.from({ length: 6 }, (_, index) => (
    <TableRow key={index}>
      <TableCell>
        <Skeleton className="h-4 w-10" />
      </TableCell>
      <TableCell>
        <div className="max-w-xl space-y-2">
          <Skeleton className="h-4 w-52 max-w-full" />
          <Skeleton className="h-3 w-72 max-w-full" />
        </div>
      </TableCell>
      <TableCell className="hidden md:table-cell">
        <div className="flex gap-1.5">
          <Skeleton className="h-5 w-14 rounded-full" />
          <Skeleton className="h-5 w-20 rounded-full" />
        </div>
      </TableCell>
      <TableCell className="hidden sm:table-cell">
        <Skeleton className="ml-auto h-4 w-10" />
      </TableCell>
      <TableCell className="hidden sm:table-cell">
        <Skeleton className="ml-auto h-4 w-10" />
      </TableCell>
      <TableCell className="hidden lg:table-cell">
        <Skeleton className="h-4 w-32" />
      </TableCell>
      <TableCell>
        <div className="flex justify-end gap-1">
          <Skeleton className="size-8" />
          <Skeleton className="size-8" />
        </div>
      </TableCell>
    </TableRow>
  ))
}
export default QuestionAdminListSkeleton