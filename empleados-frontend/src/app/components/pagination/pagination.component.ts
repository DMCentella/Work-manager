import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [NgFor, NgIf],
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent {
  @Input() currentPage: number = 0;
  @Input() totalPages: number = 0;
  @Input() maxVisible: number = 5;
  @Output() pageChange = new EventEmitter<number>();

  get pages(): number[] {
    const pages: number[] = [];
    const total = this.totalPages;
    const current = this.currentPage;
    const max = this.maxVisible;

    let start = Math.max(0, current - Math.floor(max / 2));
    let end = Math.min(total, start + max);
    if (end - start < max) start = Math.max(0, end - max);

    for (let i = start; i < end; i++) pages.push(i);
    return pages;
  }

  goTo(page: number): void {
    if (page >= 0 && page < this.totalPages) this.pageChange.emit(page);
  }
}
