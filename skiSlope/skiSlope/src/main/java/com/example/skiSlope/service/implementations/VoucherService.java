package com.example.skiSlope.service.implementations;

import com.example.skiSlope.exception.VoucherNotFoundException;
import com.example.skiSlope.model.Voucher;
import com.example.skiSlope.model.request.VoucherRequest;
import com.example.skiSlope.repository.VoucherRepository;
import com.example.skiSlope.service.definitions.VoucherServiceDefinition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class VoucherService implements VoucherServiceDefinition {

    private VoucherRepository voucherRepository;

    @Override
    public Voucher addVoucher(Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @Override
    public Optional<Voucher> getVoucherById(Long id) { return voucherRepository.findById(id); }

    @Override
    public List<Voucher> getAllVouchersByUserId(Long userId) {
        return voucherRepository.findAllByUserId(userId);
    }

    @Override
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    @Override
    public void updateVouchersData(VoucherRequest voucherRequest, Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(VoucherNotFoundException::new);
        voucher = voucherRequest.updateVoucher(voucher);
        voucherRepository.save(voucher);
    }

    @Override
    public void deleteVoucher(Long id) {
        voucherRepository.deleteById(id);
    }
}
